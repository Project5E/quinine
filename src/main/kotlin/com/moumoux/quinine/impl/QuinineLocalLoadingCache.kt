package com.moumoux.quinine.impl

import com.github.benmanes.caffeine.cache.LoadingCache
import com.moumoux.quinine.QuinineLoadingCache
import io.reactivex.Single
import kotlinx.coroutines.rx2.await

internal class QuinineLocalLoadingCache<K: Any, V>(private val cache: LoadingCache<K, Single<V>>) :
    QuinineLocalCache<K, V>(cache),
    QuinineLoadingCache<K, V> {
    override suspend fun get(key: K): V = cache.get(key)!!.await()

    override suspend fun getAll(keys: Iterable<K>): Map<K, V> =
        cache.getAll(keys).mapValues { it.value.await() }

    override fun refresh(key: K) = cache.refresh(key)

}