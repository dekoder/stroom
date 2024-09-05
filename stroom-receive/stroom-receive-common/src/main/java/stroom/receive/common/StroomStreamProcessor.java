/*
 * Copyright 2017 Crown Copyright
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

package stroom.receive.common;

import stroom.data.zip.StreamProgressMonitor;
import stroom.data.zip.StroomZipEntry;
import stroom.data.zip.StroomZipFile;
import stroom.data.zip.StroomZipFileType;
import stroom.data.zip.StroomZipNameSet;
import stroom.meta.api.AttributeMap;
import stroom.meta.api.AttributeMapUtil;
import stroom.meta.api.StandardHeaderArguments;
import stroom.proxy.StroomStatusCode;
import stroom.util.date.DateUtil;
import stroom.util.io.ByteCountInputStream;
import stroom.util.io.CloseableUtil;
import stroom.util.io.InitialByteArrayOutputStream;
import stroom.util.io.InitialByteArrayOutputStream.BufferPos;
import stroom.util.io.StreamUtil;
import stroom.util.net.HostNameUtil;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;

public class StroomStreamProcessor {

    private static final String ZERO_CONTENT = "0";
    private static final Logger LOGGER = LoggerFactory.getLogger(StroomStreamProcessor.class);
    private static volatile String hostName;

    private final AttributeMap globalAttributeMap;
    private final List<? extends StroomStreamHandler> stroomStreamHandlerList;
    private final byte[] buffer;
    private StreamProgressMonitor streamProgressMonitor = new StreamProgressMonitor("StroomStreamProcessor ");
    private boolean appendReceivedPath = true;

    @SuppressWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
    public StroomStreamProcessor(final AttributeMap attributeMap,
                                 final List<? extends StroomStreamHandler> stroomStreamHandlerList,
                                 final byte[] buffer,
                                 final String logPrefix) {
        this.globalAttributeMap = attributeMap;
        this.buffer = buffer;
        this.stroomStreamHandlerList = stroomStreamHandlerList;
    }

    public String getHostName() {
        if (hostName == null) {
            StroomStreamProcessor.hostName = HostNameUtil.determineHostName();
        }
        return hostName;
    }

    public static void setHostName(final String hostName) {
        StroomStreamProcessor.hostName = hostName;
    }

    public void setAppendReceivedPath(final boolean appendReceivedPath) {
        this.appendReceivedPath = appendReceivedPath;
    }

    public void setStreamProgressMonitor(final StreamProgressMonitor streamProgressMonitor) {
        this.streamProgressMonitor = streamProgressMonitor;
    }

    public void processRequestHeader(final HttpServletRequest httpServletRequest) {
        String guid = globalAttributeMap.get(StandardHeaderArguments.GUID);

        // Allocate a GUID if we have not got one.
        if (guid == null) {
            guid = UUID.randomUUID().toString();
            globalAttributeMap.put(StandardHeaderArguments.GUID, guid);

            // Only allocate RemoteXxx details if the GUID has not been
            // allocated.

            // Allocate remote address if not set.
            if (httpServletRequest.getRemoteAddr() != null && !httpServletRequest.getRemoteAddr().isEmpty()) {
                globalAttributeMap.put(StandardHeaderArguments.REMOTE_ADDRESS, httpServletRequest.getRemoteAddr());
            }

            // Save the time the data was received.
            globalAttributeMap.put(StandardHeaderArguments.RECEIVED_TIME, DateUtil.createNormalDateTimeString());

            // Allocate remote address if not set.
            if (httpServletRequest.getRemoteHost() != null && !httpServletRequest.getRemoteHost().isEmpty()) {
                globalAttributeMap.put(StandardHeaderArguments.REMOTE_HOST, httpServletRequest.getRemoteHost());
            }
        }
    }

    /**
     * @param inputStream
     * @param prefix
     */
    public void process(InputStream inputStream, final String prefix) {
        try {
            handleHeader();

            boolean compressed = false;

            String compression = globalAttributeMap.get(StandardHeaderArguments.COMPRESSION);

            if (compression != null && !compression.isEmpty()) {
                compression = compression.toUpperCase(StreamUtil.DEFAULT_LOCALE);
                if (!StandardHeaderArguments.VALID_COMPRESSION_SET.contains(compression)) {
                    throw new StroomStreamException(
                            StroomStatusCode.UNKNOWN_COMPRESSION, globalAttributeMap, compression);
                }
            }

            if (ZERO_CONTENT.equals(globalAttributeMap.get(StandardHeaderArguments.CONTENT_LENGTH))) {
                LOGGER.warn("process() - Skipping Zero Content " + globalAttributeMap);
                return;
            }

            if (StandardHeaderArguments.COMPRESSION_ZIP.equals(compression)) {
                // Handle a zip stream.
                processZipStream(inputStream, prefix);

            } else {
                if (StandardHeaderArguments.COMPRESSION_GZIP.equals(compression)) {
                    // We have to wrap our stream reading code in a individual
                    // try/catch so we can return to the client an error in the
                    // case of a corrupt stream.
                    try {
                        // Use the APACHE GZIP de-compressor as it handles
                        // nested compressed streams
                        inputStream = new GzipCompressorInputStream(inputStream, true);
                        compressed = true;
                    } catch (final IOException ioEx) {
                        LOGGER.debug(ioEx.getMessage(), ioEx);
                        throw new StroomStreamException(
                                StroomStatusCode.COMPRESSED_STREAM_INVALID, globalAttributeMap, ioEx.getMessage());
                    }
                }

                int read = 0;
                // Read an initial buffer full so we can see if there is any un-compressed data
                // Some apps that roll log files may create a gziped rolled log from an empty live log
                read = readToBuffer(inputStream, compressed);

                if (read == -1) {
                    LOGGER.warn("process() - Skipping Zero Content in GZIP stream" + globalAttributeMap);
                } else {
                    long totalRead = 0;
                    try {
                        handleEntryStart(StroomZipFile.SINGLE_DATA_ENTRY);
                        while (read != -1) {
                            streamProgressMonitor.progress(read);
                            handleEntryData(buffer, 0, read);
                            totalRead += read;

                            read = readToBuffer(inputStream, compressed);
                        }
                    } finally {
                        handleEntryEnd();
                    }
                    final AttributeMap entryAttributeMap = AttributeMapUtil.cloneAllowable(globalAttributeMap);
                    entryAttributeMap.put(StandardHeaderArguments.STREAM_SIZE, String.valueOf(totalRead));
                    sendHeader(StroomZipFile.SINGLE_META_ENTRY, entryAttributeMap);
                }
            }
        } catch (final IOException zex) {
            StroomStreamException.createAndThrow(zex, globalAttributeMap);
        } finally {
            CloseableUtil.closeLogAndIgnoreException(inputStream);
        }
    }

    private int readToBuffer(final InputStream inputStream, final boolean isCompressed) throws IOException {
        // We have to wrap our stream reading code in a individual
        // try/catch so we can return to the client an error in the
        // case of a corrupt stream.
        int read;
        try {
            read = StreamUtil.eagerRead(inputStream, buffer);
        } catch (final IOException ioEx) {
            if (isCompressed) {
                throw new StroomStreamException(
                        StroomStatusCode.COMPRESSED_STREAM_INVALID,
                        globalAttributeMap,
                        ioEx.getMessage());
            } else {
                throw ioEx;
            }
        }
        return read;
    }

    private void processZipStream(final InputStream inputStream, final String prefix) throws IOException {
        final ByteCountInputStream byteCountInputStream = new ByteCountInputStream(inputStream);

        final Map<String, AttributeMap> bufferedAttributeMap = new HashMap<>();
        final Map<String, Long> dataStreamSizeMap = new HashMap<>();
        final List<String> sendDataList = new ArrayList<>();
        final StroomZipNameSet stroomZipNameSet = new StroomZipNameSet(false);

        try (final ZipArchiveInputStream zipArchiveInputStream = new ZipArchiveInputStream(byteCountInputStream)) {
            ZipArchiveEntry zipEntry;
            while (true) {
                // We have to wrap our stream reading code in a individual try/catch
                // so we can return to the client an error in the case of a corrupt
                // stream.
                try {
                    // TODO See the javadoc for ZipArchiveInputStream as getNextZipEntry
                    // may return an entry that is not in the zip dictionary or it may
                    // return multiple entries with the same name. Our code probably
                    // works because we would not expect the zips to have been mutated which
                    // may cause these cases, however we are on slightly shaky ground grabbing
                    // entries without consulting the zip's dictionary.
                    zipEntry = zipArchiveInputStream.getNextEntry();
                } catch (final IOException ioEx) {
                    throw new StroomStreamException(
                            StroomStatusCode.COMPRESSED_STREAM_INVALID, globalAttributeMap, ioEx.getMessage());
                }

                if (zipEntry == null) {
                    // All done
                    break;
                }

                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("process() - " + zipEntry);
                }

                final String entryName = prefix + zipEntry.getName();
                final long uncompressedSize = zipEntry.getSize();
                final StroomZipEntry stroomZipEntry = stroomZipNameSet.add(entryName);

                if (uncompressedSize == 0) {
                    // Ideally we would want to ignore empty entries but because there may be multiple child
                    // streams for the same base name (dat/meta/ctx) we don't really want to ignore the dat if
                    // there are non-empty meta/ctx entries as that will probably cause problems elsewhere in
                    // stroom as we expect to always have a data  child stream. As the entries may be in any order
                    // we can check the dat size first, and as we are streaming we can't inspect the dictionary
                    // to find out. Thus the best we can do is warn.
                    LOGGER.warn("processZipStream() - zip entry {} is empty. {}", entryName, globalAttributeMap);
                }

                if (StroomZipFileType.Meta.equals(stroomZipEntry.getStroomZipFileType())) {
                    final AttributeMap entryAttributeMap = AttributeMapUtil.cloneAllowable(globalAttributeMap);
                    // We have to wrap our stream reading code in a individual
                    // try/catch so we can return to the client an error in the case
                    // of a corrupt stream.
                    try {
                        AttributeMapUtil.read(zipArchiveInputStream, entryAttributeMap);
                    } catch (final IOException ioEx) {
                        throw new StroomStreamException(
                                StroomStatusCode.COMPRESSED_STREAM_INVALID,
                                globalAttributeMap,
                                ioEx.getMessage());
                    }

                    if (appendReceivedPath) {
                        // Here we build up a list of stroom servers that have received
                        // the message

                        // The entry one will be initially set at the boundary Stroom
                        // server
                        final String entryReceivedServer = entryAttributeMap.get(StandardHeaderArguments.RECEIVED_PATH);

                        if (entryReceivedServer != null) {
                            if (!entryReceivedServer.contains(getHostName())) {
                                entryAttributeMap.put(StandardHeaderArguments.RECEIVED_PATH,
                                        entryReceivedServer + "," + getHostName());
                            }
                        } else {
                            entryAttributeMap.put(StandardHeaderArguments.RECEIVED_PATH, getHostName());
                        }
                    }

                    if (entryAttributeMap.containsKey(StandardHeaderArguments.STREAM_SIZE)) {
                        // Header already has stream size so just send it on
                        sendHeader(stroomZipEntry, entryAttributeMap);
                    } else {
                        // We need to add the stream size
                        // Send the data file yet ?
                        final String dataFile = stroomZipNameSet.getName(
                                stroomZipEntry.getBaseName(),
                                StroomZipFileType.Data);
                        if (dataFile != null && dataStreamSizeMap.containsKey(dataFile)) {
                            // Yes we can send the header now
                            entryAttributeMap.put(StandardHeaderArguments.STREAM_SIZE,
                                    String.valueOf(dataStreamSizeMap.get(dataFile)));
                            sendHeader(stroomZipEntry, entryAttributeMap);
                        } else {
                            // Else we have to buffer it
                            bufferedAttributeMap.put(stroomZipEntry.getBaseName(), entryAttributeMap);
                        }
                    }
                } else {
                    long totalRead = 0;
                    try {
                        handleEntryStart(stroomZipEntry);
                        int read;
                        while (true) {
                            // We have to wrap our stream reading code in a individual
                            // try/catch so we can return to the client an error in the
                            // case of a corrupt stream.
                            try {
                                read = StreamUtil.eagerRead(zipArchiveInputStream, buffer);
                            } catch (final IOException ioEx) {
                                throw new StroomStreamException(
                                        StroomStatusCode.COMPRESSED_STREAM_INVALID,
                                        globalAttributeMap,
                                        ioEx.getMessage());
                            }
                            if (read == -1) {
                                break;
                            }
                            streamProgressMonitor.progress(read);
                            handleEntryData(buffer, 0, read);
                            totalRead += read;
                        }
                    } finally {
                        handleEntryEnd();
                    }

                    if (StroomZipFileType.Data.equals(stroomZipEntry.getStroomZipFileType())) {
                        sendDataList.add(entryName);
                        dataStreamSizeMap.put(entryName, totalRead);
                    }

                    // Buffered header can now be sent as we have sent the
                    // data
                    if (stroomZipEntry.getBaseName() != null) {
                        final AttributeMap entryAttributeMap =
                                bufferedAttributeMap.remove(stroomZipEntry.getBaseName());
                        if (entryAttributeMap != null) {
                            entryAttributeMap.put(StandardHeaderArguments.STREAM_SIZE, String.valueOf(totalRead));
                            try {
                                handleEntryStart(new StroomZipEntry(
                                        null,
                                        stroomZipEntry.getBaseName(),
                                        StroomZipFileType.Meta));
                                final byte[] headerBytes = AttributeMapUtil.toByteArray(entryAttributeMap);
                                handleEntryData(headerBytes, 0, headerBytes.length);
                            } finally {
                                handleEntryEnd();
                            }
                        }
                    }
                }
            }
        }

        if (stroomZipNameSet.getBaseNameSet().isEmpty()) {
            // A zip stream with no entries is always 22 bytes in size.
            if (byteCountInputStream.getCount() > 22) {
                throw new StroomStreamException(
                        StroomStatusCode.COMPRESSED_STREAM_INVALID, globalAttributeMap, "No Zip Entries");
            } else {
                LOGGER.warn("processZipStream() - Zip stream with no entries! {}", globalAttributeMap);
            }
        }

        // Add missing headers
        for (final String baseName : stroomZipNameSet.getBaseNameList()) {
            final String headerName = stroomZipNameSet.getName(baseName, StroomZipFileType.Meta);
            // Send Generic Header
            if (headerName == null) {
                final String dataFileName = stroomZipNameSet.getName(baseName, StroomZipFileType.Data);
                final AttributeMap entryAttributeMap = AttributeMapUtil.cloneAllowable(globalAttributeMap);
                entryAttributeMap.put(StandardHeaderArguments.STREAM_SIZE,
                        String.valueOf(dataStreamSizeMap.remove(dataFileName)));
                sendHeader(new StroomZipEntry(null, baseName, StroomZipFileType.Meta), entryAttributeMap);
            }
        }
    }

    public void closeHandlers() {
        for (final StroomStreamHandler handler : stroomStreamHandlerList) {
            if (handler instanceof Closeable) {
                CloseableUtil.closeLogAndIgnoreException((Closeable) handler);
            }
        }
    }

    private void sendHeader(final StroomZipEntry stroomZipEntry, final AttributeMap attributeMap) throws IOException {
        try {
            handleEntryStart(stroomZipEntry);

            // Try and use the buffer
            InitialByteArrayOutputStream byteArrayOutputStream = null;
            try (final InitialByteArrayOutputStream initialByteArrayOutputStream =
                    new InitialByteArrayOutputStream(buffer)) {

                byteArrayOutputStream = initialByteArrayOutputStream;
                AttributeMapUtil.write(attributeMap, initialByteArrayOutputStream);
            }

            final BufferPos bufferPos = byteArrayOutputStream.getBufferPos();
            handleEntryData(bufferPos.getBuffer(), 0, bufferPos.getBufferPos());
        } finally {
            handleEntryEnd();
        }
    }

    private void handleHeader() throws IOException {
        for (final StroomStreamHandler stroomStreamHandler : stroomStreamHandlerList) {
            if (stroomStreamHandler instanceof StroomHeaderStreamHandler) {
                ((StroomHeaderStreamHandler) stroomStreamHandler).handleHeader(globalAttributeMap);
            }
        }
    }

    private void handleEntryStart(final StroomZipEntry stroomZipEntry) throws IOException {
        for (final StroomStreamHandler stroomStreamHandler : stroomStreamHandlerList) {
            stroomStreamHandler.handleEntryStart(stroomZipEntry);
        }
    }

    private void handleEntryEnd() throws IOException {
        for (final StroomStreamHandler stroomStreamHandler : stroomStreamHandlerList) {
            stroomStreamHandler.handleEntryEnd();
        }
    }

    private void handleEntryData(final byte[] data, final int off, final int len) throws IOException {
        for (final StroomStreamHandler stroomStreamHandler : stroomStreamHandlerList) {
            stroomStreamHandler.handleEntryData(data, off, len);
        }
    }
}
