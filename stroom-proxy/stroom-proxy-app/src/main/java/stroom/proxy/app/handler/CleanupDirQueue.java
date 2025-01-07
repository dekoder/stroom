package stroom.proxy.app.handler;

import stroom.proxy.app.DataDirProvider;
import stroom.util.io.FileUtil;
import stroom.util.logging.LambdaLogger;
import stroom.util.logging.LambdaLoggerFactory;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Singleton
public class CleanupDirQueue {

    private static final LambdaLogger LOGGER = LambdaLoggerFactory.getLogger(CleanupDirQueue.class);

    private final Path dir;

    @Inject
    CleanupDirQueue(final DataDirProvider dataDirProvider) {
        dir = dataDirProvider.get().resolve("99_deleting");
        DirUtil.ensureDirExists(dir);
        FileUtil.deleteContents(dir);
    }

    public void add(final Path sourceDir) {
        try {
            // We will move before delete to help ensure we don't end up partially deleting dir contents in place.
            final Path deleteDir = dir.resolve(sourceDir.getFileName());
            Files.move(sourceDir, deleteDir, StandardCopyOption.ATOMIC_MOVE);
            FileUtil.deleteDir(deleteDir);
        } catch (final IOException e) {
            LOGGER.error(() -> "Failed to cleanup dir: " + FileUtil.getCanonicalPath(sourceDir), e);
            throw new UncheckedIOException(e);
        }
    }
}
