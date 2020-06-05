package com.moumoux.quinine.reactive.impl

import com.github.benmanes.caffeine.cache.LoadingCache
import com.moumoux.quinine.reactive.QuinineLoadingCache
import io.reactivex.Single

internal class QuinineLocalLoadingCache<K : Any, V>(private val cache: LoadingCache<K, Single<V>>) :
    QuinineLocalCache<K, V>(cache), QuinineLoadingCache<K, V> {
    override fun get(key: K): Single<V> = cache.get(key)!!

    override fun getAll(keys: Iterable<K>): Map<K, Single<V>> = cache.getAll(keys)

    override fun refresh(key: K) = cache.refresh(key)
}