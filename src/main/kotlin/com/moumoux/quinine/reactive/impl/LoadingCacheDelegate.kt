package com.moumoux.quinine.reactive.impl

import com.moumoux.quinine.reactive.LoadingCache
import io.reactivex.Observable
import io.reactivex.Single
import com.github.benmanes.caffeine.cache.LoadingCache as CaffeineLoadingCache


class LoadingCacheDelegate<K: Any, V>(override val cache: CaffeineLoadingCache<K, Single<V>>) : CacheDelegate<K, V>(cache),
    LoadingCache<K, V> {
    override fun get(key: K) = cache.get(key)!!

    override fun getAll(keys: Iterable<K>): Map<K, Single<V>> = cache.getAll(keys)

    override fun refresh(key: K) = cache.refresh(key)

    override fun subscribeUpdate(channel: Observable<K>) {
        notifyCacheDelegate.subscriptions[channel] = channel.subscribe { refresh(it) }
    }
}