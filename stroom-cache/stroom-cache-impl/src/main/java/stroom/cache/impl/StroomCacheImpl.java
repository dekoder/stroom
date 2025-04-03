package stroom.cache.impl;

import stroom.util.cache.CacheConfig;
import stroom.util.metrics.Metrics;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.inject.Provider;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

class StroomCacheImpl<K, V> extends AbstractStroomCache<K, V> {

    public StroomCacheImpl(final String name,
                           final Supplier<CacheConfig> cacheConfigSupplier,
                           final BiConsumer<K, V> removalNotificationConsumer,
                           final Provider<Metrics> metricsProvider) {
        super(name, cacheConfigSupplier, removalNotificationConsumer, metricsProvider);
        rebuild();
    }

    @Override
    Cache<K, V> createCacheFromBuilder(final Caffeine<K, V> cacheBuilder) {
        return cacheBuilder.build();
    }
}
