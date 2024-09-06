/*
 * Copyright 2016 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package stroom.data.store.impl.fs;

import stroom.util.io.BasicStreamCloser;
import stroom.util.io.SeekableOutputStream;
import stroom.util.io.StreamCloser;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class BlockGZipOutputStream extends OutputStream implements SeekableOutputStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockGZipOutputStream.class);

    // The main buffer used (typically holds 2 longs and the the GZIP output).
    // We use 'big' buffer (that holds the whole block) as we go back and write
    // the block size at the end of processing.
    private final BlockByteArrayOutputStream mainBuffer;
    // Our index buffer we append on at the end.
    private final BlockByteArrayOutputStream indexBuffer;
    // Use to help track non-closed streams
    private final StreamCloser streamCloser = new BasicStreamCloser();
    private final SeekableOutputStream mainStream;
    // The stream - we hold a buffer onto it as well
    private BufferedOutputStream currentStreamBuffer;
    private GzipCompressorOutputStream currentStreamGzip;
    // The block size we are using
    private int blockSize;
    // The current 'logical' uncompressed data item we have written
    private long position = 0;
    // The current block number we are on
    private long blockCount = 0;
    // ((blockCount+1) * blockSize)
    private long currentBlockEndPos = 0;
    private boolean closed;

    BlockGZipOutputStream(final SeekableOutputStream mainStream) throws IOException {
        this(mainStream, BlockGZIPConstants.DEFAULT_BLOCK_SIZE);
    }

    BlockGZipOutputStream(final SeekableOutputStream mainStream, final int blockSize) throws IOException {
        this.mainStream = mainStream;
        this.blockSize = blockSize;
        this.mainBuffer = new BlockByteArrayOutputStream();
        this.indexBuffer = new BlockByteArrayOutputStream();

        // Mark the start of the index with a magic marker
        indexBuffer.write(BlockGZIPConstants.MAGIC_MARKER);

        // Write a marker
        mainBuffer.write(BlockGZIPConstants.BLOCK_GZIP_V1_IDENTIFIER);
        // At the start of the block file write the block size an empty place
        // for the index offset and the marker
        // we
        mainBuffer.writeLong(blockSize);
        // Uncompressed Data Length
        mainBuffer.writeLong(0);
        // Index POS
        mainBuffer.writeLong(0);
        // End POS
        mainBuffer.writeLong(0);

        flushMainBuffer();

        // Make sure the streams are closed.
        streamCloser.add(mainBuffer).add(indexBuffer);
    }

    /**
     * Write the buffer to the file and reset it.
     */
    private void flushMainBuffer() throws IOException {
        mainStream.write(mainBuffer.getRawBuffer(), 0, mainBuffer.size());
        mainBuffer.reset();
    }

    /**
     * @return Our current position (in uncompressed bytes)
     */
    @Override
    public long getPosition() {
        return position;
    }

    /**
     * Start a new burst of GZIP Block Content (with a marker).
     */
    private void endGzipBlock() throws IOException {
        blockCount++;

        currentStreamBuffer.flush();
        currentStreamGzip.flush();
        currentStreamGzip.finish();

        // Block Compressed size is size of stream less magic marker less block
        // size header (2 longs)
        long rawBlockSize = mainBuffer.size() - BlockGZIPConstants.LONG_BYTES - BlockGZIPConstants.LONG_BYTES;
        mainBuffer.overwriteLongAtOffset(BlockGZIPConstants.LONG_BYTES, rawBlockSize);

        flushMainBuffer();

        currentStreamBuffer = null;
        currentStreamGzip = null;
    }

    private void startGzipBlock() throws IOException {
        // At what point to we start a new block
        currentBlockEndPos = (blockCount + 1) * blockSize;

        // Record the start Pos
        final long currentRawBlockStartPos = mainStream.getPosition();

        // Record the index
        indexBuffer.writeLong(currentRawBlockStartPos);

        // Marker
        mainBuffer.write(BlockGZIPConstants.MAGIC_MARKER);

        // Write some bytes for the long we will do later
        mainBuffer.writeLong(0);

        // Connect a new GZIP stream
        currentStreamGzip = new GzipCompressorOutputStream(mainBuffer);
        currentStreamBuffer = new BufferedOutputStream(currentStreamGzip, FileSystemUtil.STREAM_BUFFER_SIZE);
    }

    @Override
    public void write(final int b) throws IOException {
        if (currentStreamBuffer == null) {
            startGzipBlock();
        }
        // Write a single byte
        currentStreamBuffer.write(b);
        position++;

        // Have we moved onto the next block?
        if (position == currentBlockEndPos) {
            endGzipBlock();
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void write(final byte[] b) throws IOException {
        // Delegate
        write(b, 0, b.length);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void write(final byte[] bytes, final int offset, final int length) throws IOException {
        if (currentStreamBuffer == null) {
            startGzipBlock();
        }

        // Find out how many bytes are left to write in the current block
        int bytesLeftInBlock = (int) (currentBlockEndPos - position);

        // These bytes will fit in this block
        if (length <= bytesLeftInBlock) {
            currentStreamBuffer.write(bytes, offset, length);
            position += length;

            if (length == bytesLeftInBlock) {
                endGzipBlock();
            }

        } else {
            // We need to split this up - write the first half
            currentStreamBuffer.write(bytes, offset, bytesLeftInBlock);
            position += bytesLeftInBlock;
            endGzipBlock();
            // Now have ago again with the reminder
            write(bytes, offset + bytesLeftInBlock, length - bytesLeftInBlock);
        }

    }

    @Override
    public void close() throws IOException {
        try {
            if (!closed) {
                closed = true;

                if (currentStreamBuffer != null) {
                    // End the data stream
                    endGzipBlock();
                }

                // Record where we are going to start writing the index
                final long idxStart = mainStream.getPosition();

                // Append the Index
                mainStream.write(indexBuffer.getRawBuffer(), 0, indexBuffer.size());

                // Now Record the EOF
                final long eof = mainStream.getPosition();

                // Seek back to the start to write the above stats.
                // Write the Index Post back in the header
                mainStream.seek(BlockGZIPConstants.BLOCK_GZIP_V1_IDENTIFIER.length + BlockGZIPConstants.LONG_BYTES);
                // Write the uncompressed stream size

                mainBuffer.reset();
                // Size of Uncompressed Data
                mainBuffer.writeLong(position);
                // And the Index Start POS
                mainBuffer.writeLong(idxStart);
                // And the End File Pos
                mainBuffer.writeLong(eof);

                flushMainBuffer();

                mainStream.close();
            }
        } catch (final RuntimeException e) {
            LOGGER.error("Unable to close output stream!", e);
        } finally {
            try {
                streamCloser.close();
            } catch (final IOException e) {
                LOGGER.error(e.getMessage(), e);
            } finally {
                super.close();
            }
        }
    }

    @Override
    public void flush() throws IOException {
        // We ignore flush to the file as the stream store does not allow it for
        // performance reasons

        // We only flush to our buffer (not all the way to the file)
        if (currentStreamBuffer != null) {
            currentStreamBuffer.flush();
        }
    }

    long getBlockCount() {
        return blockCount;
    }

//    long getBlockSize() {
//        return blockSize;
//    }

    @Override
    public long getSize() {
        return position;
    }

    @Override
    public void seek(long pos) {
        throw new UnsupportedOperationException();
    }
}
