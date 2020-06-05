package com.moumoux.quinine.reactive

import io.reactivex.Single

interface QuinineLoadingCache<K : Any, V> : QuinineCache<K, V> {
    fun get(key: K): Single<V>
    fun getAll(keys: Iterable<K>): Map<K, Single<V>>
    fun refresh(key: K)
}