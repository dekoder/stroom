package stroom.data.zip;

import stroom.meta.api.AttributeMap;
import stroom.meta.api.AttributeMapUtil;
import stroom.task.api.TaskContext;
import stroom.util.io.WrappedOutputStream;
import stroom.util.zip.ZipUtil;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class StroomZipOutputStreamImpl implements StroomZipOutputStream {

    private static final String LOCK_EXTENSION = ".lock";
    private static final Logger LOGGER = LoggerFactory.getLogger(StroomZipOutputStreamImpl.class);
    private final Path file;
    private final Path lockFile;
    private final ZipArchiveOutputStream zipOutputStream;
    private final StreamProgressMonitor streamProgressMonitor;
    private StroomZipNameSet stroomZipNameSet;
    private boolean inEntry = false;
    private long entryCount = 0;

    public StroomZipOutputStreamImpl(final Path file) throws IOException {
        this(file, null);
    }

    public StroomZipOutputStreamImpl(final Path file, final TaskContext taskContext) throws IOException {
        this(file, taskContext, true);
    }

    public StroomZipOutputStreamImpl(final Path path, final TaskContext taskContext, final boolean monitorEntries)
            throws IOException {
        Path file = path;
        Path lockFile = path.getParent().resolve(path.getFileName().toString() + LOCK_EXTENSION);

        if (Files.deleteIfExists(file)) {
            LOGGER.warn("deleted file " + file);
        }
        if (Files.deleteIfExists(lockFile)) {
            LOGGER.warn("deleted file " + lockFile);
        }

        this.file = file;

        // Ensure the lock file is created so that the parent dir is not cleaned up before we start writing data.
        this.lockFile = Files.createFile(lockFile);

        streamProgressMonitor = new StreamProgressMonitor(taskContext, "Write");
        final OutputStream rawOutputStream = Files.newOutputStream(lockFile);
        final OutputStream bufferedOutputStream = new BufferedOutputStream(rawOutputStream, BufferSizeUtil.get());
        final OutputStream progressOutputStream = new FilterOutputStreamProgressMonitor(bufferedOutputStream,
                streamProgressMonitor);
        zipOutputStream = ZipUtil.createOutputStream(progressOutputStream);
        if (monitorEntries) {
            stroomZipNameSet = new StroomZipNameSet(false);
        }
    }

//    public StroomZipOutputStreamImpl(final OutputStream outputStream) throws IOException {
//        this(outputStream, null);
//    }
//
//    public StroomZipOutputStreamImpl(final OutputStream outputStream, final Monitor monitor) throws IOException {
//        this.monitor = monitor;
//
//        file = null;
//        lockFile = null;
//        streamProgressMonitor = new StreamProgressMonitor(monitor, "Write");
//        zipOutputStream = new ZipOutputStream(
//                new FilterOutputStreamProgressMonitor(new BufferedOutputStream(outputStream), streamProgressMonitor));
//        stroomZipNameSet = new StroomZipNameSet(false);
//    }

    @Override
    public long getProgressSize() {
        if (streamProgressMonitor != null) {
            return streamProgressMonitor.getTotalBytes();
        }
        return -1;
    }

    @Override
    public OutputStream addEntry(final String name) throws IOException {
        if (inEntry) {
            throw new RuntimeException("Failed to close last entry");
        }
        entryCount++;
        inEntry = true;
        if (Thread.currentThread().isInterrupted()) {
            throw new IOException("Progress Stopped");
        }
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("addEntry() - " + file + " - " + name + " - adding");
        }
        if (stroomZipNameSet != null) {
            stroomZipNameSet.add(name);
        }
        zipOutputStream.putArchiveEntry(new ZipArchiveEntry(name));
        return new WrappedOutputStream(zipOutputStream) {
            @Override
            public void close() throws IOException {
                zipOutputStream.closeArchiveEntry();
                inEntry = false;
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("addEntry() - " + file + " - " + name + " - closed");
                }
            }
        };
    }

    public long getEntryCount() {
        return entryCount;
    }

    @Override
    public void addMissingAttributeMap(final AttributeMap attributeMap) throws IOException {
        if (stroomZipNameSet == null) {
            throw new RuntimeException("You can only add missing meta data if you are monitoring entries");

        }
        for (final String baseName : stroomZipNameSet.getBaseNameList()) {
            if (stroomZipNameSet.getName(baseName, StroomZipFileType.Meta) == null) {
                zipOutputStream.putArchiveEntry(new ZipArchiveEntry(baseName + StroomZipFileType.Meta.getExtension()));
                AttributeMapUtil.write(attributeMap, zipOutputStream);
                zipOutputStream.closeArchiveEntry();
            }
        }
    }

    @Override
    public void close() throws IOException {
        // ZIP's don't like to be empty !
        if (entryCount == 0) {
            closeDelete();
        } else {
            zipOutputStream.close();
            if (lockFile != null) {
                try {
                    Files.move(lockFile, file);
                } catch (final RuntimeException e) {
                    throw new IOException("Failed to rename file " + lockFile + " to " + file);
                }
            }
        }
    }

    @Override
    public void closeDelete() throws IOException {
        // ZIP's don't like to be empty !
        if (entryCount == 0) {
            final OutputStream os = addEntry(new StroomZipEntry("NULL.DAT",
                    "NULL",
                    StroomZipFileType.Data).getFullName());
            os.write("NULL".getBytes(CharsetConstants.DEFAULT_CHARSET));
            os.close();
        }

        zipOutputStream.close();
        if (lockFile != null) {
            try {
                Files.delete(lockFile);
            } catch (final RuntimeException e) {
                throw new IOException("Failed to delete file " + lockFile);
            }
        }
    }

    public Path getFile() {
        return file;
    }

    public Path getLockFile() {
        return lockFile;
    }

    @Override
    public String toString() {
        return file.toString();
    }
}
