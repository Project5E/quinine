package com.moumoux.quinine.reactive.impl

import com.github.benmanes.caffeine.cache.Cache as CaffeineCache
import com.moumoux.quinine.common.BaseCache
import com.moumoux.quinine.common.NotifyCache
import com.moumoux.quinine.common.NotifyUpdateCache
import com.moumoux.quinine.common.impl.BaseCacheDelegate
import com.moumoux.quinine.common.impl.NotifyCacheDelegate
import com.moumoux.quinine.reactive.Cache
import io.reactivex.Observable
import io.reactivex.Single

open class CacheDelegate<K : Any, V>(
    internal open val cache: CaffeineCache<K, Single<V>>,
    private val baseCacheDelegate: BaseCache<K, V> = BaseCacheDelegate(cache),
    internal val notifyCacheDelegate: NotifyCache<K, V> = NotifyCacheDelegate(baseCacheDelegate)
) : BaseCache<K, V> by baseCacheDelegate,
    NotifyCache<K, V> by notifyCacheDelegate,
    NotifyUpdateCache<K, V>,
    Cache<K, V> {
    override fun put(key: K, value: Single<V>) = cache.put(key, value.cache())
    override fun putAll(map: Map<out K, Single<V>>) {
        map.forEach { (k, u) -> cache.put(k, u.cache()) }
    }

    override fun get(key: K, mappingFunction: (K) -> Single<V>) = cache.get(key) { mappingFunction(it).cache() }!!

    override fun getIfPresent(key: K): Single<V>? = cache.getIfPresent(key)
    override fun getAllPresent(keys: Iterable<K>): Map<K, Single<V>> = cache.getAllPresent(keys)

    override fun <T : Any> subscribeUpdate(channel: Observable<T>, transformer: (T) -> Pair<K, V>) {
        notifyCacheDelegate.subscriptions[channel] = channel.subscribe {
            val (k, v) = transformer(it)
            put(k, Single.just(v))
        }
    }
}