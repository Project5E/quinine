package com.moumoux.quinine.common

import io.reactivex.Single

interface GetCache<K : Any, T> {
    /**
     * Returns the value associated with the [key] in this cache, obtaining that value from the
     * [mappingFunction] if necessary. This method provides a simple substitute for the
     * conventional "if cached, return; otherwise create, cache and return" pattern.
     * <p>
     * If the specified key is not already associated with a value, attempts to compute its value
     * using the given mapping function and enters it into this cache unless <pre>null</pre>. The entire
     * method invocation is performed atomically, so the function is applied at most once per key.
     * Some attempted update operations on this cache by other threads may be blocked while the
     * computation is in progress, so the computation should be short and simple, and must not attempt
     * to update any other mappings of this cache.
     * <p>
     * <b>Warning:</b> [mappingFunction] <b>must not</b> attempt to update any other mappings of this cache.
     *
     * @param key the key with which the specified value is to be associated
     * @param mappingFunction the function to compute a value
     * @return the current (existing or computed) value associated with the specified key, or null if
     *         the computed value is null
     * @throws IllegalStateException if the computation detectably attempts a recursive update to this
     *         cache that would otherwise never complete
     * @throws RuntimeException or Error if the mappingFunction does so, in which case the mapping is
     *         left unestablished
     */
    fun get(key: K, mappingFunction: (K) -> T): T

    /**
     * Returns the value associated with the [key] in this cache, or <pre>null</pre> if there is no
     * cached value for the [key].
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or <pre>null</pre> if this cache contains
     *         no mapping for the key
     */
    fun getIfPresent(key: K): T?

    /**
     * Returns a map of the values associated with the [keys] in this cache. The returned map
     * will only contain entries which are already present in the cache.
     * <p>
     * Note that duplicate elements in [keys], as determined by [Any.equals] , will be ignored.
     *
     * @param keys the keys whose associated values are to be returned
     * @return the readonly mapping of keys to values for the specified keys found in this cache
     */
    fun getAllPresent(keys: Iterable<K>): Map<K, T>
}