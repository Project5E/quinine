package com.moumoux.quinine.reactive.impl

import com.github.benmanes.caffeine.cache.Cache
import com.moumoux.quinine.QuinineCacheStats
import com.moumoux.quinine.reactive.QuinineCache
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import java.util.concurrent.ConcurrentHashMap

internal open class QuinineLocalCache<K : Any, V>(private val cache: Cache<K, Single<V>>) :
    QuinineCache<K, V> {
    override val estimatedSize: Long
        get() = cache.estimatedSize()
    override val stats: QuinineCacheStats
        get() = QuinineCacheStats.from(cache.stats())

    internal val subscriptions: MutableMap<Observable<*>, Disposable> = ConcurrentHashMap()

    override fun cleanUp() = cache.cleanUp()

    override fun invalidate(key: Any) = cache.invalidate(key)
    override fun invalidateAll(keys: Iterable<K>) = cache.invalidateAll(keys)
    override fun invalidateAll() = cache.invalidateAll()

    override fun put(key: K, value: Single<V>) = cache.put(key, value.cache())
    override fun putAll(map: Map<out K, Single<V>>) {
        map.forEach { (k, u) -> cache.put(k, u.cache()) }
    }

    override fun get(key: K, mappingFunction: (K) -> Single<V>) = cache.get(key) { mappingFunction(it).cache() }!!

    override fun getIfPresent(key: Any): Single<V>? = cache.getIfPresent(key)
    override fun getAllPresent(keys: Iterable<K>): Map<K, Single<V>> = cache.getAllPresent(keys)

    override fun subscribeInvalidate(channel: Observable<K>) {
        subscriptions[channel] = channel.subscribe { invalidate(it) }
    }

    override fun <T : Any> subscribeInvalidate(channel: Observable<T>, transformer: (T) -> K) {
        subscriptions[channel] = channel.subscribe {
            invalidate(transformer(it))
        }
    }

    override fun <T : Any> subscribeUpdate(channel: Observable<T>, transformer: (T) -> Pair<K, V>) {
        subscriptions[channel] = channel.subscribe {
            val (k, v) = transformer(it)
            put(k, Single.just(v))
        }
    }

    override fun unsubscribe(channel: Observable<*>) {
        subscriptions[channel]?.dispose()
        subscriptions.remove(channel)
    }
}