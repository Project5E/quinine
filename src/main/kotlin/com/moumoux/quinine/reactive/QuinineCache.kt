package com.moumoux.quinine.reactive

import com.moumoux.quinine.QuinineCacheStats
import io.reactivex.Single

interface QuinineCache<K : Any, V> {
    val stats: QuinineCacheStats
    val estimatedSize: Long

    fun cleanUp()

    fun invalidate(key: Any)
    fun invalidateAll(keys: Iterable<*>)
    fun invalidateAll()

    fun put(key: K, value: Single<V>)
    fun putAll(map: Map<out K, Single<V>>)
    fun get(key: K, mappingFunction: (K) -> Single<V>): Single<V>
    fun getIfPresent(key: Any): Single<V>
    fun getAllPresent(keys: Iterable<*>): Map<K, Single<V>>
}