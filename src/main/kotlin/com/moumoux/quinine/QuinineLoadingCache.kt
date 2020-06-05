package com.moumoux.quinine

/**
 * A semi-persistent mapping from keys to values. Values are automatically loaded by the cache,
 * and are stored in the cache until either evicted or manually invalidated.
 * <p>
 * Implementations of this interface are expected to be thread-safe, and can be safely accessed
 * by multiple concurrent threads.
 *
 * @author light.tsing@gmail.com (Akase Cho)
 * @param K the most general key type this builder will be able to create caches for.
 * @param V the most general value type this builder will be able to create caches for.
 */
interface QuinineLoadingCache<K: Any, V> : QuinineCache<K, V> {
    /**
     * Returns the value associated with the [key] in this cache, obtaining that value from
     * loader if necessary.
     * <p>
     * If another call to [get] is currently loading the value for the [key], this thread
     * simply waits for that thread to finish and returns its loaded value. Note that multiple threads
     * can concurrently load values for distinct keys.
     * <p>
     * If the specified key is not already associated with a value, attempts to compute its value and
     * enters it into this cache unless <pre>null</pre>. The entire method invocation is performed
     * atomically, so the function is applied at most once per key. Some attempted update operations
     * on this cache by other threads may be blocked while the computation is in progress, so the
     * computation should be short and simple, and must not attempt to update any other mappings of
     * this cache.
     *
     * @param key key with which the specified value is to be associated
     * @return the current (existing or computed) value associated with the specified key, or null if
     *         the computed value is null
     * @throws IllegalStateException if the computation detectably attempts a recursive update to this
     *         cache that would otherwise never complete
     * @throws RuntimeException or Error if the loader does so, in which case the mapping
     *         is left unestablished
     */
    suspend fun get(key: K): V

    /**
     * Returns a map of the values associated with the [keys], creating or retrieving those
     * values if necessary. The returned map contains entries that were already cached, combined with
     * the newly loaded entries; it will never contain null keys or values.
     * <p>
     * Caches loaded by a loader will issue a single request to for all keys which are not already
     * present in the cache. All entries returned by loader will be stored in the cache, over-writing
     * any previously cached values. If another call to [get] tries to load the value for a key in
     * [keys], implementations may either have that thread load the entry or simply wait for
     * this thread to finish and returns the loaded value. In the case of overlapping non-blocking
     * loads, the last load to complete will replace the existing entry. Note that multiple threads
     * can concurrently load values for distinct keys.
     * <p>
     * Note that duplicate elements in [keys], as determined by [Any.equals], will be
     * ignored.
     *
     * @param keys the keys whose associated values are to be returned
     * @return the readonly mapping of keys to values for the specified keys in this cache
     * @throws RuntimeException or Error if the loader does so, if
     *         loader returns <pre>null</pre>, returns a map containing null keys or
     *         values, or fails to return an entry for each requested key. In all cases, the mapping
     *         is left unestablished
     */
    suspend fun getAll(keys: Iterable<K>): Map<K, V>

    /**
     * Loads a new value for the [key] , asynchronously. While the new value is loading the
     * previous value (if any) will continue to be returned by [get] unless it is evicted.
     * If the new value is loaded successfully it will replace the previous value in the cache; if an
     * exception is thrown while refreshing the previous value will remain, <i>and the exception will
     * be logged (using [java.util.logging.Logger]) and swallowed</i>.
     * <p>
     * Caches loaded by a loader will be called if the cache currently contains a value for the [key],
     * and load otherwise. Loading is asynchronous by delegating to the default executor.
     *
     * @param key key with which a value may be associated
     */
    fun refresh(key: K)
}