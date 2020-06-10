package com.moumoux.quinine.common

import io.reactivex.Single

interface PutCache<K : Any, V> {
    /**
     * Associates the [value] with the [key] in this cache. If the cache previously contained
     * a value associated with the [key], the old value is replaced by the new [value]
     * <p>
     * Prefer [get] when using the conventional "if cached, return; otherwise create, cache and return" pattern.
     *
     * @param key the key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     */
    fun put(key: K, value: V)

    /**
     * Copies all of the mappings from the specified map to the cache. The effect of this call is
     * equivalent to that of calling [put] on this map once for each mapping from key
     * [K] to value [V] in the specified map. The behavior of this operation is undefined
     * if the specified map is modified while the operation is in progress.
     *
     * @param map the mappings to be stored in this cache
     */
    fun putAll(map: Map<out K, V>)
}