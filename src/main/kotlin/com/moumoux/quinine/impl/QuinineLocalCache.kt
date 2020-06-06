package com.moumoux.quinine.impl

import com.github.benmanes.caffeine.cache.Cache
import com.moumoux.quinine.QuinineCache
import com.moumoux.quinine.QuinineCacheStats
import io.reactivex.Single
import kotlinx.coroutines.*
import kotlinx.coroutines.rx2.await
import kotlin.coroutines.CoroutineContext

internal open class QuinineLocalCache<K : Any, V>(private val cache: Cache<K, Single<V>>) :
    QuinineCache<K, V>, CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO

    override val estimatedSize: Long
        get() = cache.estimatedSize()
    override val stats: QuinineCacheStats
        get() = QuinineCacheStats.from(cache.stats())

    override fun cleanUp() = cache.cleanUp()

    override fun invalidate(key: Any) = cache.invalidate(key)
    override fun invalidateAll(keys: Iterable<K>) = cache.invalidateAll(keys)
    override fun invalidateAll() = cache.invalidateAll()

    override fun put(key: K, value: V) = cache.put(key, Single.just(value).cache())
    override fun putAll(map: Map<out K, V>) {
        map.forEach { (k, u) -> cache.put(k, Single.just(u).cache()) }
    }

    override suspend fun get(key: K, mappingFunction: suspend (K) -> V): V {
        return coroutineScope {
            cache.get(key) {
                Single.create<V> { emitter ->
                    launch { emitter.onSuccess(mappingFunction(it)) }
                }.cache()
            }!!.await()
        }
    }

    override suspend fun getIfPresent(key: K): V? = cache.getIfPresent(key)?.await()
    override suspend fun getAllPresent(keys: Iterable<K>): Map<K, V> =
        cache.getAllPresent(keys).mapValues { it.value.await() }

}