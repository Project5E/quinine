package com.moumoux.quinine

interface QuinineLoadingCache<K: Any, V> : QuinineCache<K, V> {
    suspend fun get(key: K): V
    suspend fun getAll(keys: Iterable<K>): Map<K, V>
    fun refresh(key: K)
}