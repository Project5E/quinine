package com.moumoux.quinine

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.LoadingCache
import io.reactivex.Single
import kotlinx.coroutines.rx2.await

interface QuinineCache<K, V> {
    fun estimatedSize(): Long
    fun cleanUp()
    // fun policy(): Policy<K, Single<V>>
    // fun stats(): CacheStats
    fun invalidate(key: Any)
    fun invalidateAll(keys: Iterable<*>)
    fun invalidateAll()

    fun put(key: K, value: V)
    fun putAll(map: Map<out K, V>)
    suspend fun get(key: K, mappingFunction: (K) -> V): V
    suspend fun getIfPresent(key: Any): V?
    suspend fun getAllPresent(keys: MutableIterable<K>): Map<K, V>
}

interface QuinineLoadingCache<K, V>: QuinineCache<K, V> {
    suspend fun get(key: K): V
    suspend fun getAll(keys: Iterable<K>): Map<K, V>
    fun refresh(key: K)
}

internal open class QuinineLocalCache<K, V>(private val cache: Cache<K, Single<V>>): QuinineCache<K, V> {
    override fun estimatedSize(): Long = cache.estimatedSize()
    override fun cleanUp() = cache.cleanUp()

    override fun invalidate(key: Any) = cache.invalidate(key)
    override fun invalidateAll(keys: Iterable<*>) = cache.invalidateAll(keys)
    override fun invalidateAll() = cache.invalidateAll()

    override fun put(key: K, value: V) = cache.put(key, Single.just(value).cache())
    override fun putAll(map: Map<out K, V>) {
        map.forEach { (k, u) -> cache.put(k, Single.just(u).cache()) }
    }

    override suspend fun get(key: K, mappingFunction: (K) -> V): V {
        return cache.get(key) {
            Single.create<V> { emitter -> emitter.onSuccess(mappingFunction(it)) }.cache()
        }!!.await()
    }
    override suspend fun getIfPresent(key: Any): V? = cache.getIfPresent(key)?.await()
    override suspend fun getAllPresent(keys: MutableIterable<K>): Map<K, V> =
        cache.getAllPresent(keys).mapValues { it.value.await() }

}

internal class QuinineLocalLoadingCache<K, V>(private val cache: LoadingCache<K, Single<V>>): QuinineLocalCache<K, V>(cache), QuinineLoadingCache<K, V> {
    override suspend fun get(key: K): V = cache.get(key)!!.await()

    override suspend fun getAll(keys: Iterable<K>): Map<K, V> =
        cache.getAll(keys).mapValues { it.value.await() }

    override fun refresh(key: K) = cache.refresh(key)

}