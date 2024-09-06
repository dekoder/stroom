package stroom.proxy.repo;

import stroom.task.api.ExecutorProvider;
import stroom.task.api.TaskContextFactory;
import stroom.task.api.ThreadPoolImpl;
import stroom.task.shared.ThreadPool;
import stroom.util.date.DateUtil;
import stroom.util.io.BufferFactory;
import stroom.util.io.FileUtil;
import stroom.util.scheduler.Scheduler;
import stroom.util.scheduler.SimpleCron;
import stroom.util.thread.CustomThreadFactory;
import stroom.util.thread.StroomThreadGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * Class that reads repositories.
 */
@Singleton
public final class ProxyRepositoryReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyRepositoryReader.class);

    private final TaskContextFactory taskContextFactory;
    private final BufferFactory bufferFactory;
    private final ProxyRepositoryManager proxyRepositoryManager;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    private final Provider<ProxyRepositoryReaderConfig> proxyRepositoryReaderConfigProvider;
    private final StreamHandlerFactory handlerFactory;

    /**
     * Our worker thread
     */
    private volatile CompletableFuture<Void> readerThread;

    /**
     * CRON trigger - can be null
     */
    private final Scheduler scheduler;

    private final ExecutorProvider executorProvider;
    private final ThreadPool threadPool;
    private final Map<ThreadPool, ExecutorService> executorServiceMap = new ConcurrentHashMap<>();
    private final AtomicBoolean finish = new AtomicBoolean();

    @Inject
    ProxyRepositoryReader(final TaskContextFactory taskContextFactory,
                          final BufferFactory bufferFactory,
                          final ProxyRepositoryManager proxyRepositoryManager,
                          final Provider<ProxyRepositoryReaderConfig> proxyRepositoryReaderConfigProvider,
                          final StreamHandlerFactory handlerFactory) {
        this.taskContextFactory = taskContextFactory;
        this.bufferFactory = bufferFactory;
        this.proxyRepositoryReaderConfigProvider = proxyRepositoryReaderConfigProvider;
        this.handlerFactory = handlerFactory;
        this.proxyRepositoryManager = proxyRepositoryManager;
        this.scheduler = createScheduler(proxyRepositoryReaderConfigProvider.get().getReadCron());

        threadPool = new ThreadPoolImpl("Proxy Repository Reader");

        executorProvider = new ExecutorProvider() {
            @Override
            public Executor get() {
                return get(threadPool);
            }

            @Override
            public Executor get(final ThreadPool threadPool) {
                return executorServiceMap.computeIfAbsent(
                        threadPool,
                        k -> {
                            final ThreadGroup poolThreadGroup = new ThreadGroup(StroomThreadGroup.instance(),
                                    threadPool.getName());
                            final CustomThreadFactory taskThreadFactory = new CustomThreadFactory(
                                    threadPool.getName() + " #", poolThreadGroup, threadPool.getPriority());
                            return Executors.newCachedThreadPool(taskThreadFactory);
                        });
            }
        };
    }

    private static Scheduler createScheduler(final String simpleCron) {
        if (simpleCron != null && !simpleCron.isEmpty()) {
            return SimpleCron.compile(simpleCron).createScheduler();
        }

        return null;
    }

    private synchronized void startReading() {
        if (readerThread == null) {
            finish.set(false);

            readerThread = CompletableFuture.runAsync(this::process);
        }
    }

    private synchronized void stopReading() {
        if (readerThread != null) {
            terminate();

            lock.lock();
            try {
                condition.signalAll();
            } finally {
                lock.unlock();
            }

            boolean waiting = true;
            while (waiting) {
                try {
                    LOGGER.info("stopReading() - Waiting for read thread to stop");
                    readerThread.get(1, TimeUnit.SECONDS);
                    waiting = false;
                } catch (final TimeoutException e) {
                    // Ignore.
                } catch (final InterruptedException e) {
                    LOGGER.warn("Thread interrupted");
                    waiting = false;

                    // Reset the interrupt flag
                    Thread.currentThread().interrupt();
                } catch (final ExecutionException | RuntimeException e) {
                    LOGGER.error(e.getMessage(), e);
                    waiting = false;
                }
            }
        }
    }

    /**
     * Main Working Thread - Keep looping until we have been told to finish
     */
    private void process() {
        lock.lock();
        try {
            while (!isTerminated()) {
                try {
                    condition.await(1, TimeUnit.SECONDS);
                } catch (final InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);

                    // Continue to interrupt this thread.
                    Thread.currentThread().interrupt();
                }

                if (!isTerminated()) {
                    // Only do the work if we are not on a timer or our timer
                    // says we should fire.
                    if (scheduler != null && !scheduler.execute()) {
                        continue;
                    }

                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Cron Match at " + DateUtil.createNormalDateTimeString());
                    }

                    try {
                        doRunWork();
                    } catch (final RuntimeException e) {
                        LOGGER.error("Unhandled exception coming out of doRunWork()", e);
                    }
                }
            }
        } finally {
            lock.unlock();
        }

        LOGGER.info("Completed ... Thread Exit");
    }

    void doRunWork() {
        if (proxyRepositoryManager == null) {
            return;
        }
        final List<StroomZipRepository> readyToProcessList = proxyRepositoryManager.getReadableRepository();

        for (final StroomZipRepository readyToProcess : readyToProcessList) {
            if (isTerminated()) {
                return;
            }

            // Only process the thing if we have some outgoing handlers.
            final List<StreamHandler> handlers = handlerFactory.addSendHandlers(new ArrayList<>());
            if (handlers.size() > 0) {
                final Provider<FileSetProcessor> fileSetProcessorProvider = () -> new ProxyForwardingFileSetProcessor(
                        handlerFactory,
                        bufferFactory);

                final ProxyRepositoryReaderConfig proxyRepositoryReaderConfig =
                        proxyRepositoryReaderConfigProvider.get();

                final RepositoryProcessor repositoryProcessor = new RepositoryProcessor(
                        executorProvider,
                        taskContextFactory,
                        fileSetProcessorProvider,
                        FileUtil.getCanonicalPath(readyToProcess.getRootDir()),
                        proxyRepositoryReaderConfig.getForwardThreadCount(),
                        proxyRepositoryReaderConfig.getWorkQueueCapacity(),
                        proxyRepositoryReaderConfig.getMaxFileScan(),
                        proxyRepositoryReaderConfig.getMaxConcurrentMappedFiles(),
                        proxyRepositoryReaderConfig.getMaxAggregation(),
                        proxyRepositoryReaderConfig.getMaxStreamSize());

                repositoryProcessor.process();
            }
            // Otherwise just clean.

            // If the root of this repo is also our configured rootRepoDir then we don't want to delete the
            // repo's root on clean as it causes problems in docker containers. Deleting a configured directory
            // may also cause confusion for admins.
            final boolean deleteRootDirectory = !readyToProcess.getRootDir()
                    .equals(proxyRepositoryManager.getRootRepoDir());
            readyToProcess.clean(deleteRootDirectory);
        }
    }

    public void stop() {
        terminate();
        LOGGER.info("stop() - Stopping Reader Thread");
        stopReading();
        LOGGER.info("stop() - Stopped  Reader Thread");

        LOGGER.info("stop() - Stopping Executors");
        executorServiceMap.values().forEach(ExecutorService::shutdownNow);
        executorServiceMap.clear();
        LOGGER.info("stop() - Stopped  Executors");
    }

    public void start() {
        startReading();
    }

    private boolean isTerminated() {
        if (finish.get() || Thread.currentThread().isInterrupted()) {
            Thread.currentThread().interrupt();
            return true;
        }
        return false;
    }

    private void terminate() {
        finish.set(true);
        Thread.currentThread().interrupt();
    }
}
