package com.moumoux.quinine.reactive

import com.github.benmanes.caffeine.cache.LoadingCache
import io.reactivex.Single

interface QuinineLoadingCache<K, V> : QuinineCache<K, V> {
    fun get(key: K): Single<V>
    fun getAll(keys: Iterable<K>): Map<K, Single<V>>
    fun refresh(key: K)
}

internal class QuinineLocalLoadingCache<K, V>(private val cache: LoadingCache<K, Single<V>>) :
    QuinineLocalCache<K, V>(cache), QuinineLoadingCache<K, V> {
    override fun get(key: K): Single<V> = cache.get(key)!!

    override fun getAll(keys: Iterable<K>): Map<K, Single<V>> = cache.getAll(keys)

    override fun refresh(key: K) = cache.refresh(key)
}