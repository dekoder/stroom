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

package stroom.proxy.repo;

import stroom.task.api.ExecutorProvider;
import stroom.task.api.TaskContext;
import stroom.task.api.TaskContextFactory;
import stroom.task.api.ThreadPoolImpl;
import stroom.task.shared.ThreadPool;
import stroom.util.concurrent.WorkQueue;
import stroom.util.date.DateUtil;
import stroom.util.io.AbstractFileVisitor;
import stroom.util.io.FileUtil;
import stroom.util.logging.LogExecutionTime;
import stroom.util.logging.LogUtil;
import stroom.util.shared.ModelStringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.inject.Provider;

/**
 * Class that reads a nested directory tree of stroom zip files.
 */
public final class RepositoryProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryProcessor.class);

    private final ExecutorProvider executorProvider;
    private final TaskContextFactory taskContextFactory;
    private final Provider<FileSetProcessor> fileSetProcessorProvider;

    private final int threadCount;
    private final int workQueueCapacity;
    private final int maxFileScan;
    private final int maxConcurrentMappedFiles;
    private final int maxFilesPerAggregate;
    private final long maxUncompressedFileSize;
    private final Path repoPath;
    private final String repoDir;

    public RepositoryProcessor(final ExecutorProvider executorProvider,
                               final TaskContextFactory taskContextFactory,
                               final Provider<FileSetProcessor> fileSetProcessorProvider,
                               final String proxyDir,
                               final int threadCount,
                               final int workQueueCapacity,
                               final int maxFileScan,
                               final int maxConcurrentMappedFiles,
                               final int maxFilesPerAggregate,
                               final long maxUncompressedFileSize) {
        this.executorProvider = executorProvider;
        this.taskContextFactory = taskContextFactory;
        this.fileSetProcessorProvider = fileSetProcessorProvider;
        this.threadCount = threadCount;
        this.workQueueCapacity = workQueueCapacity;
        this.maxFileScan = maxFileScan;
        this.maxConcurrentMappedFiles = maxConcurrentMappedFiles;
        this.maxFilesPerAggregate = maxFilesPerAggregate;
        this.maxUncompressedFileSize = maxUncompressedFileSize;
        repoPath = Paths.get(proxyDir);
        repoDir = FileUtil.getCanonicalPath(repoPath);
    }

    /**
     * Process the Stroom zip repository,
     */
    public void process() {
        final Consumer<TaskContext> consumer = taskContext -> {
            final LogExecutionTime logExecutionTime = new LogExecutionTime();
            LOGGER.info("Started processing repository {}", repoDir);

            taskContext.info(() -> LogUtil.message(
                    "Process started {}, maxFileScan {}, maxConcurrentMappedFiles {}, " +
                            "maxFilesPerAggregate {}, maxUncompressedFileSize {}",
                    DateUtil.createNormalDateTimeString(System.currentTimeMillis()),
                    ModelStringUtil.formatCsv(maxFileScan),
                    ModelStringUtil.formatCsv(maxConcurrentMappedFiles),
                    ModelStringUtil.formatCsv(maxFilesPerAggregate),
                    ModelStringUtil.formatIECByteSizeString(maxUncompressedFileSize)));

            try {
                if (Files.isDirectory(repoPath)) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Scanning " + repoDir);
                    }

                    final ErrorReceiver errorReceiver = (path, message) ->
                            addErrorMessage(path, message, true);

                    // Keep processing until we no longer reach the maximum file scan limit.
                    boolean reachedFileScanLimit;
                    do {
                        // Break down the zip repository so that all zip files only contain a single stream.
                        // We do this so that we can form new aggregates that contain less files than the
                        // maximum number or are smaller than the maximum size
                        reachedFileScanLimit = fragmentZipFiles(executorProvider,
                                taskContext,
                                taskContextFactory,
                                threadCount,
                                workQueueCapacity,
                                errorReceiver);

                        // Aggregate the zip files.
                        aggregateZipFiles(executorProvider,
                                taskContext,
                                taskContextFactory,
                                threadCount,
                                workQueueCapacity,
                                errorReceiver);

                    } while (reachedFileScanLimit);

                    LOGGER.debug("Completed");

                } else {
                    LOGGER.debug("Repo dir " + repoDir + " not found");
                }
            } catch (final Exception e) {
                LOGGER.error(e.getMessage(), e);
            }

            LOGGER.info("Completed in {}", logExecutionTime);
        };
        final TaskContext parentTaskContext = taskContextFactory.current();
        final Runnable runnable = taskContextFactory.childContext(parentTaskContext,
                "Proxy Repository Processor",
                consumer);
        runnable.run();
    }

    private boolean fragmentZipFiles(final ExecutorProvider executorProvider,
                                     final TaskContext parentContext,
                                     final TaskContextFactory taskContextFactory,
                                     final int threadCount,
                                     final int workQueueCapacity,
                                     final ErrorReceiver errorReceiver) {
        final Function<TaskContext, Boolean> function = taskContext -> {
            final ZipFragmenterFileProcessor zipFragmenter = new ZipFragmenterFileProcessor(
                    executorProvider, taskContext, taskContextFactory, threadCount, workQueueCapacity, errorReceiver);

            final AtomicInteger fileCount = new AtomicInteger();
            try {
                Files.walkFileTree(repoPath,
                        EnumSet.of(FileVisitOption.FOLLOW_LINKS),
                        Integer.MAX_VALUE,
                        new AbstractFileVisitor() {
                            @Override
                            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                                if (PartsPathUtil.isPartsDir(dir)) {
                                    return FileVisitResult.SKIP_SUBTREE;
                                }
                                return super.preVisitDirectory(dir, attrs);
                            }

                            @Override
                            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
                                taskContext.info(() -> FileUtil.getCanonicalPath(file));

                                if (Thread.currentThread().isInterrupted() || fileCount.get() >= maxFileScan) {
                                    return FileVisitResult.TERMINATE;
                                }

                                // Process only raw zip repo files, i.e. files that have not already been created
                                // by the fragmenting process.
                                final String fileName = file.getFileName().toString();
                                if (fileName.endsWith(StroomZipRepository.ZIP_EXTENSION) &&
                                        !PartsPathUtil.isPart(file)) {
                                    fileCount.incrementAndGet();
                                    zipFragmenter.process(file);
                                }

                                return FileVisitResult.CONTINUE;
                            }
                        });
            } catch (final IOException | RuntimeException e) {
                LOGGER.error(e.getMessage(), e);
            }

            // Wait for the fragmenter to complete.
            zipFragmenter.await();

            // Did we reach the file scan limit?
            return fileCount.get() >= maxFileScan;
        };
        final Supplier<Boolean> supplier = taskContextFactory.childContextResult(parentContext,
                "Fragmenting Repository - " + repoDir,
                function);
        return supplier.get();
    }

    private void aggregateZipFiles(final ExecutorProvider executorProvider,
                                   final TaskContext parentContext,
                                   final TaskContextFactory taskContextFactory,
                                   final int threadCount,
                                   final int workQueueCapacity,
                                   final ErrorReceiver errorReceiver) {
        final Consumer<TaskContext> consumer = taskContext -> {
            final ZipInfoConsumer zipInfoConsumer = new ZipInfoConsumer(
                    maxFilesPerAggregate,
                    maxConcurrentMappedFiles,
                    maxUncompressedFileSize,
                    errorReceiver,
                    fileSetProcessorProvider,
                    executorProvider,
                    parentContext,
                    taskContextFactory,
                    threadCount,
                    workQueueCapacity);
            final ZipInfoExtractor zipInfoExtractor = new ZipInfoExtractor(errorReceiver);
            final ZipInfoExtractorFileProcessor fileProcessor = new ZipInfoExtractorFileProcessor(
                    zipInfoExtractor,
                    zipInfoConsumer,
                    executorProvider,
                    parentContext,
                    taskContextFactory,
                    threadCount,
                    workQueueCapacity);

            try {
                Files.walkFileTree(repoPath,
                        EnumSet.of(FileVisitOption.FOLLOW_LINKS),
                        Integer.MAX_VALUE,
                        new AbstractFileVisitor() {
                            @Override
                            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                                if (PartsPathUtil.isPartsDir(dir)) {
                                    final Path originalZipFile = PartsPathUtil.createParentPartsZipFile(dir);

                                    // Make sure the parts directory that this file is in is not a partially
                                    // fragmented output directory.
                                    if (Files.exists(originalZipFile)) {
                                        return FileVisitResult.SKIP_SUBTREE;
                                    }
                                }
                                return super.preVisitDirectory(dir, attrs);
                            }

                            @Override
                            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
                                taskContext.info(() -> FileUtil.getCanonicalPath(file));

                                if (Thread.currentThread().isInterrupted()) {
                                    return FileVisitResult.TERMINATE;
                                }

                                // Process only part files, i.e. files that have been created by the fragmenting
                                // process.
                                if (PartsPathUtil.isPart(file)) {
                                    fileProcessor.process(file, attrs);
                                }

                                return FileVisitResult.CONTINUE;
                            }
                        });
            } catch (final IOException | RuntimeException e) {
                LOGGER.error(e.getMessage(), e);
            }

            // Wait for the file processor to complete.
            fileProcessor.await();

            // Complete processing remaining file sets.
            zipInfoConsumer.complete();
        };
        final Runnable runnable = taskContextFactory.childContext(parentContext,
                "Aggregating Repository - " + repoDir,
                consumer);
        runnable.run();
    }

    private void addErrorMessage(final Path path, final String msg, final boolean bad) {
        ErrorFileUtil.addErrorMessage(path, msg, bad);
    }

    private static class ZipInfoConsumer implements Consumer<ZipInfo> {

        private final int maxFilesPerAggregate;
        private final int maxConcurrentMappedFiles;
        private final long maxUncompressedFileSize;
        private final ErrorReceiver errorReceiver;
        private final Provider<FileSetProcessor> fileSetProcessorProvider;
        private final TaskContext parentContext;
        private final TaskContextFactory taskContextFactory;

        private final Map<FileSetKey, FileSet> fileSetMap = new ConcurrentHashMap<>();
        private final WorkQueue workQueue;

        private int totalMappedFiles;

        ZipInfoConsumer(final int maxFilesPerAggregate,
                        final int maxConcurrentMappedFiles,
                        final long maxUncompressedFileSize,
                        final ErrorReceiver errorReceiver,
                        final Provider<FileSetProcessor> fileSetProcessorProvider,
                        final ExecutorProvider executorProvider,
                        final TaskContext parentContext,
                        final TaskContextFactory taskContextFactory,
                        final int threadCount,
                        final int workQueueCapacity) {
            this.maxFilesPerAggregate = maxFilesPerAggregate;
            this.maxConcurrentMappedFiles = maxConcurrentMappedFiles;
            this.maxUncompressedFileSize = maxUncompressedFileSize;
            this.errorReceiver = errorReceiver;
            this.fileSetProcessorProvider = fileSetProcessorProvider;
            this.parentContext = parentContext;
            this.taskContextFactory = taskContextFactory;

            final ThreadPool threadPool = new ThreadPoolImpl("File Set Processor");
            final Executor executor = executorProvider.get(threadPool);
            workQueue = new WorkQueue(executor, threadCount, workQueueCapacity);
        }

        @Override
        public synchronized void accept(final ZipInfo zipInfo) {
            final FileSetKey key = zipInfo.getKey();
            if (key.getFeedName() == null || key.getFeedName().isBlank()) {
                errorReceiver.onError(zipInfo.getPath(), "Unable to find feed in header??");

            } else {
                LOGGER.debug("{} belongs to {}", zipInfo.getPath(), key);

                FileSet fileSet = fileSetMap.computeIfAbsent(key, FileSet::new);


                final long uncompressedSize = fileSet.getTotalUncompressedFileSize() + zipInfo.getUncompressedSize();
                final long entryCount = fileSet.getTotalZipEntryCount() + zipInfo.getZipEntryCount();
                // See if the file set will overflow if we add this file.
                if (fileSet.getFiles().size() > 0 &&
                        (uncompressedSize > maxUncompressedFileSize || entryCount > maxFilesPerAggregate)) {

                    // Send the file set for processing.
                    processFileSet(fileSet);

                    // Create a new file set.
                    fileSet = fileSetMap.computeIfAbsent(key, FileSet::new);
                }

                // The file set is not full so add the file.
                fileSet.add(zipInfo);
                totalMappedFiles++;

                // If the file set is now full send it for processing.
                if (fileSet.getTotalUncompressedFileSize() >= maxUncompressedFileSize ||
                        fileSet.getTotalZipEntryCount() >= maxFilesPerAggregate) {

                    // Send the file set for processing.
                    processFileSet(fileSet);

                } else {

                    // If we have reached the maximum number of concurrent mapped files then we need to send the
                    // largest set for processing.
                    if (totalMappedFiles >= maxConcurrentMappedFiles) {
                        final List<FileSet> sortedList = fileSetMap
                                .values()
                                .stream()
                                .sorted(Comparator.comparing(FileSet::getTotalUncompressedFileSize).reversed())
                                .collect(Collectors.toList());

                        // Remove the biggest file set from the map.
                        final FileSet biggestFileSet = sortedList.get(0);

                        // Send the file set for processing.
                        processFileSet(biggestFileSet);
                    }
                }
            }
        }

        private synchronized void processFileSet(final FileSet fileSet) {
            // Remove the full file set.
            fileSetMap.remove(fileSet.getKey());

            // Reduce the total number of files we have mapped.
            totalMappedFiles -= fileSet.getFiles().size();

            try {
                final Runnable runnable = taskContextFactory.childContext(parentContext,
                        "File Set Processor",
                        taskContext -> {
                            final FileSetProcessor fileSetProcessor = fileSetProcessorProvider.get();
                            fileSetProcessor.process(fileSet);
                        });
                workQueue.exec(runnable);
            } catch (final RuntimeException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        public synchronized void complete() {
            // Send all remaining file sets for processing.
            fileSetMap.values().forEach(this::processFileSet);

            try {
                workQueue.join();
            } catch (final RuntimeException e) {
                LOGGER.error(e.getMessage(), e);
                throw e;
            }
        }
    }

    private static class ZipFragmenterFileProcessor {

        private final ZipFragmenter zipFragmenter;
        private final TaskContext parentContext;
        private final TaskContextFactory taskContextFactory;
        private final WorkQueue queue;

        ZipFragmenterFileProcessor(final ExecutorProvider executorProvider,
                                   final TaskContext parentContext,
                                   final TaskContextFactory taskContextFactory,
                                   final int threadCount,
                                   final int workQueueCapacity,
                                   final ErrorReceiver errorReceiver) {
            final ThreadPool fileInspectorThreadPool = new ThreadPoolImpl("Proxy File Fragmenter");
            final Executor executor = executorProvider.get(fileInspectorThreadPool);
            this.parentContext = parentContext;
            this.taskContextFactory = taskContextFactory;
            zipFragmenter = new ZipFragmenter(errorReceiver);
            queue = new WorkQueue(executor, threadCount, workQueueCapacity);
        }

        public void process(final Path file) {
            try {
                final Runnable runnable = taskContextFactory.childContext(
                        parentContext,
                        "Fragment",
                        taskContext -> {
                            // Process the file to extract ZipInfo
                            taskContext.info(() -> FileUtil.getCanonicalPath(file));

                            if (!Thread.currentThread().isInterrupted()) {
                                zipFragmenter.fragment(file);
                            }
                        });
                queue.exec(runnable);
            } catch (final RuntimeException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        public void await() {
            try {
                queue.join();
            } catch (final RuntimeException e) {
                LOGGER.error(e.getMessage(), e);
                throw e;
            }
        }
    }

    private static class ZipInfoExtractorFileProcessor {

        private final ZipInfoExtractor zipInfoExtractor;
        private final TaskContext parentContext;
        private final TaskContextFactory taskContextFactory;
        private final Consumer<ZipInfo> zipInfoConsumer;
        private final WorkQueue workQueue;

        ZipInfoExtractorFileProcessor(final ZipInfoExtractor zipInfoExtractor,
                                      final Consumer<ZipInfo> zipInfoConsumer,
                                      final ExecutorProvider executorProvider,
                                      final TaskContext parentContext,
                                      final TaskContextFactory taskContextFactory,
                                      final int threadCount,
                                      final int workQueueCapacity) {
            this.zipInfoExtractor = zipInfoExtractor;
            this.zipInfoConsumer = zipInfoConsumer;
            this.parentContext = parentContext;
            this.taskContextFactory = taskContextFactory;

            final ThreadPool fileInspectorThreadPool = new ThreadPoolImpl("Proxy File Inspection");
            final Executor executor = executorProvider.get(fileInspectorThreadPool);
            workQueue = new WorkQueue(executor, threadCount, workQueueCapacity);
        }

        public void process(final Path file, final BasicFileAttributes attrs) {
            try {
                final Runnable runnable = taskContextFactory.childContext(
                        parentContext,
                        "Extract Zip Info",
                        taskContext -> {
                            // Process the file to extract ZipInfo
                            taskContext.info(() -> FileUtil.getCanonicalPath(file));

                            if (!Thread.currentThread().isInterrupted()) {
                                final ZipInfo zipInfo = zipInfoExtractor.extract(file, attrs);
                                if (zipInfo != null) {
                                    if (LOGGER.isTraceEnabled()) {
                                        LOGGER.trace("Consume: " + zipInfo);
                                    }
                                    processZipInfo(zipInfo);
                                }
                            }
                        });
                workQueue.exec(runnable);
            } catch (final RuntimeException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        /**
         * This method is synchronised so that the zip info consumer can operate in a thread safe manner.
         *
         * @param zipInfo The zip info to process.
         */
        private void processZipInfo(final ZipInfo zipInfo) {
            zipInfoConsumer.accept(zipInfo);
        }

        public void await() {
            try {
                workQueue.join();
            } catch (final RuntimeException e) {
                LOGGER.error(e.getMessage(), e);
                throw e;
            }
        }
    }
}
