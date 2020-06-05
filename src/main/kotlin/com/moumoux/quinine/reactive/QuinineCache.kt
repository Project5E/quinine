package com.moumoux.quinine.reactive

import com.github.benmanes.caffeine.cache.Cache
import com.moumoux.quinine.QuinineCacheStats
import io.reactivex.Single

interface QuinineCache<K, V> {
    val stats: QuinineCacheStats
    val estimatedSize: Long

    fun cleanUp()

    fun invalidate(key: Any)
    fun invalidateAll(keys: Iterable<*>)
    fun invalidateAll()

    fun put(key: K, value: Single<V>)
    fun putAll(map: Map<out K, Single<V>>)
    fun get(key: K, mappingFunction: (K) -> Single<V>): Single<V>
    fun getIfPresent(key: Any): Single<V>
    fun getAllPresent(keys: Iterable<*>): Map<K, Single<V>>
}

internal open class QuinineLocalCache<K, V>(private val cache: Cache<K, Single<V>>) : QuinineCache<K, V> {
    override val estimatedSize: Long
        get() = cache.estimatedSize()
    override val stats: QuinineCacheStats
        get() = QuinineCacheStats.from(cache.stats())

    override fun cleanUp() = cache.cleanUp()

    override fun invalidate(key: Any) = cache.invalidate(key)
    override fun invalidateAll(keys: Iterable<*>) = cache.invalidateAll(keys)
    override fun invalidateAll() = cache.invalidateAll()

    override fun put(key: K, value: Single<V>) = cache.put(key, value.cache())
    override fun putAll(map: Map<out K, Single<V>>) {
        map.forEach { (k, u) -> cache.put(k, u.cache()) }
    }

    override fun get(key: K, mappingFunction: (K) -> Single<V>) = cache.get(key) { mappingFunction(it).cache() }!!

    override fun getIfPresent(key: Any): Single<V> = cache.getIfPresent(key)!!
    override fun getAllPresent(keys: Iterable<*>): Map<K, Single<V>> = cache.getAllPresent(keys)
}