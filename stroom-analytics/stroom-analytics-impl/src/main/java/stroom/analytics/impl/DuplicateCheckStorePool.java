package stroom.analytics.impl;

import stroom.util.logging.LambdaLogger;
import stroom.util.logging.LambdaLoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

class DuplicateCheckStorePool<K, V> {

    private static final LambdaLogger LOGGER = LambdaLoggerFactory.getLogger(DuplicateCheckStorePool.class);

    private final Map<K, References<V>> map = new ConcurrentHashMap<>();
    private final Function<K, V> objectFactory;
    private final Consumer<V> borrowHandler;
    private final Consumer<V> releaseHandler;
    private final Consumer<V> destructionHandler;

    public DuplicateCheckStorePool(final Function<K, V> objectFactory,
                                   final Consumer<V> borrowHandler,
                                   final Consumer<V> releaseHandler,
                                   final Consumer<V> destructionHandler) {
        this.objectFactory = objectFactory;
        this.borrowHandler = borrowHandler;
        this.releaseHandler = releaseHandler;
        this.destructionHandler = destructionHandler;
    }

    public <T> T use(final K key, final Function<V, T> consumer) {
        final V store = borrow(key);
        try {
            return consumer.apply(store);
        } finally {
            release(key);
        }
    }

    public V borrow(final K key) {
        final References<V> refs = map.compute(key,
                (k, v) -> {
                    References<V> references = v;
                    if (v == null) {
                        final V newValue = objectFactory.apply(k);
                        references = new References<>(newValue);
                    }

                    references.borrow();
                    if (borrowHandler != null) {
                        try {
                            borrowHandler.accept(references.object);
                        } catch (final RuntimeException e) {
                            LOGGER.error(e::getMessage, e);
                        }
                    }
                    return references;
                });
        return refs.object;
    }

    public void release(final K key) {
        map.compute(key,
                (k, v) -> {
                    if (v == null) {
                        throw new RuntimeException("Attempt to release object that doesn't exist");
                    }

                    final int count = v.release();
                    if (releaseHandler != null) {
                        try {
                            releaseHandler.accept(v.object);
                        } catch (final RuntimeException e) {
                            LOGGER.error(e::getMessage, e);
                        }
                    }

                    if (count == 0) {
                        if (destructionHandler != null) {
                            try {
                                destructionHandler.accept(v.object);
                            } catch (final RuntimeException e) {
                                LOGGER.error(e::getMessage, e);
                            }
                        }
                        return null;
                    }

                    return v;
                });
    }

    private static class References<V> {

        private final V object;
        private volatile int referenceCount;

        public References(final V object) {
            this.object = object;
        }

        public synchronized void borrow() {
            referenceCount++;
        }

        public synchronized int release() {
            referenceCount--;
            if (referenceCount < 0) {
                throw new RuntimeException("referenceCount < 0");
            }
            return referenceCount;
        }
    }
}
