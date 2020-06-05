package com.moumoux.quinine

import com.github.benmanes.caffeine.cache.LoadingCache
import io.reactivex.Single
import kotlinx.coroutines.rx2.await

interface QuinineLoadingCache<K, V> : QuinineCache<K, V> {
    suspend fun get(key: K): V
    suspend fun getAll(keys: Iterable<K>): Map<K, V>
    fun refresh(key: K)
}

internal class QuinineLocalLoadingCache<K, V>(private val cache: LoadingCache<K, Single<V>>) :
    QuinineLocalCache<K, V>(cache), QuinineLoadingCache<K, V> {
    override suspend fun get(key: K): V = cache.get(key)!!.await()

    override suspend fun getAll(keys: Iterable<K>): Map<K, V> =
        cache.getAll(keys).mapValues { it.value.await() }

    override fun refresh(key: K) = cache.refresh(key)

}