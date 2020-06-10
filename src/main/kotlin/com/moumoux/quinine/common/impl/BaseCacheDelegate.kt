package com.moumoux.quinine.common.impl

import com.github.benmanes.caffeine.cache.Cache
import com.moumoux.quinine.CacheStats
import com.moumoux.quinine.common.BaseCache
import io.reactivex.Single

internal class BaseCacheDelegate<K : Any, V>(internal val cache: Cache<K, Single<V>>): BaseCache<K, V> {
    override val estimatedSize: Long
        get() = cache.estimatedSize()
    override val stats: CacheStats
        get() = CacheStats.from(cache.stats())

    override fun invalidate(key: K) = cache.invalidate(key)
    override fun invalidateAll(keys: Iterable<K>) = cache.invalidateAll(keys)
    override fun invalidateAll() = cache.invalidateAll()
    override fun cleanUp() = cache.cleanUp()
}