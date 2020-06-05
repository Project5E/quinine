package com.moumoux.quinine

import com.github.benmanes.caffeine.cache.Cache
import io.reactivex.Single
import kotlinx.coroutines.rx2.await
import java.util.concurrent.Executors

interface QuinineCache<K, V> {
    val stats: QuinineCacheStats
    val estimatedSize: Long

    fun cleanUp()

    fun invalidate(key: Any)
    fun invalidateAll(keys: Iterable<*>)
    fun invalidateAll()

    fun put(key: K, value: V)
    fun putAll(map: Map<out K, V>)
    suspend fun get(key: K, mappingFunction: (K) -> V): V
    suspend fun getIfPresent(key: Any): V?
    suspend fun getAllPresent(keys: Iterable<*>): Map<K, V>
}

internal open class QuinineLocalCache<K, V>(private val cache: Cache<K, Single<V>>) : QuinineCache<K, V> {
    private val loaderExecutor = Executors.newSingleThreadExecutor()

    override val estimatedSize: Long
        get() = cache.estimatedSize()
    override val stats: QuinineCacheStats
        get() = QuinineCacheStats.from(cache.stats())

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
            Single.create<V> { emitter ->
                loaderExecutor.execute { emitter.onSuccess(mappingFunction(it)) }
            }.cache()
        }!!.await()
    }

    override suspend fun getIfPresent(key: Any): V? = cache.getIfPresent(key)?.await()
    override suspend fun getAllPresent(keys: Iterable<*>): Map<K, V> =
        cache.getAllPresent(keys).mapValues { it.value.await() }

}
