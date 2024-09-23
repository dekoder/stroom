package stroom.lmdb2;

import stroom.util.concurrent.UncheckedInterruptedException;
import stroom.util.logging.LambdaLogger;
import stroom.util.logging.LambdaLoggerFactory;

import jakarta.inject.Provider;

import java.util.concurrent.Executor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class LmdbWriter {

    private static final LambdaLogger LOGGER = LambdaLoggerFactory.getLogger(LmdbWriter.class);

    private final LmdbEnv env;
    private final ReentrantLock lock;
    private final Condition notFull;
    private final Condition notEmpty;
    private boolean closed;
    private Consumer<WriteTxn> consumer;

    public LmdbWriter(final Provider<Executor> executorProvider,
                      final LmdbEnv env) {
        this.env = env;
        lock = new ReentrantLock();
        notFull = lock.newCondition();
        notEmpty = lock.newCondition();

        // Start transfer loop.
        executorProvider.get().execute(this::transfer);
    }

    public synchronized void write(final Consumer<WriteTxn> consumer) {
        put(consumer, false);
    }

    public synchronized void flush() {
        put(WriteTxn::commit, false);
    }

    public synchronized void close() {
        put(null, true);
    }

    private void put(final Consumer<WriteTxn> newConsumer,
                     final boolean newClosedState) {
        try {
            final ReentrantLock lock = this.lock;
            lock.lockInterruptibly();
            try {
                if (closed) {
                    throw new RuntimeException("Closed");
                }

                // Ensure transfer has finished consuming the previous item.
                while (consumer != null) {
                    notFull.await();
                }

                consumer = newConsumer;
                closed = newClosedState;
                notEmpty.signal();

                // Wait for transfer to consume the item.
                while (consumer != null) {
                    notFull.await();
                }
            } finally {
                lock.unlock();
            }
        } catch (final InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UncheckedInterruptedException(e);
        }
    }

    private void transfer() {
        try (final WriteTxn writeTxn = env.writeTxn()) {
            try {
                while (!closed) {
                    final ReentrantLock lock = this.lock;
                    lock.lockInterruptibly();
                    try {
                        while (!closed && consumer == null) {
                            notEmpty.await();
                        }
                        try {
                            if (consumer != null) {
                                consumer.accept(writeTxn);
                            }
                        } finally {
                            consumer = null;
                            notFull.signal();
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            } finally {
                LOGGER.debug(() -> "close called");
                LOGGER.trace(() -> "close()", new RuntimeException("close"));
                try {
                    // Final commit.
                    writeTxn.commit();
                } catch (final RuntimeException e) {
                    LOGGER.error(e::getMessage, e);
                }
            }
        } catch (final InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UncheckedInterruptedException(e);
        }
    }
}
