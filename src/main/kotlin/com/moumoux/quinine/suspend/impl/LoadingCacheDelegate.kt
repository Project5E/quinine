package com.moumoux.quinine.suspend.impl

import com.moumoux.quinine.suspend.LoadingCache
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.rx2.await
import com.github.benmanes.caffeine.cache.LoadingCache as CaffeineLoadingCache


class LoadingCacheDelegate<K: Any, V>(override val cache: CaffeineLoadingCache<K, Single<V>>) : CacheDelegate<K, V>(cache),
    LoadingCache<K, V> {
    override suspend fun get(key: K): V = cache.get(key)!!.await()

    override suspend fun getAll(keys: Iterable<K>): Map<K, V> =
        cache.getAll(keys).mapValues { it.value.await() }

    override fun refresh(key: K) = cache.refresh(key)

    override fun subscribeUpdate(channel: Observable<K>) {
        notifyCacheDelegate.subscriptions[channel] = channel.subscribe { refresh(it) }
    }
}