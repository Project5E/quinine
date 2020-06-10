package com.moumoux.quinine.reactive

import com.moumoux.quinine.QuinineCacheStats
import io.reactivex.Observable
import io.reactivex.Single

/**
 * A semi-persistent mapping from keys to values. Cache entries are manually added using
 * [get] or [put], and are stored in the cache until either evicted or manually invalidated.
 * <p>
 * Implementations of this interface are expected to be thread-safe, and can be safely accessed by
 * multiple concurrent threads.
 *
 * @author light.tsing@gmail.com (Akase Cho)
 * @param K the most general key type this builder will be able to create caches for.
 * @param V the most general value type this builder will be able to create caches for.
 */
interface QuinineCache<K : Any, V> {
    /**
     * Returns a current snapshot of this cache's cumulative statistics. All statistics are
     * initialized to zero, and are monotonically increasing over the lifetime of the cache.
     * <p>
     * Due to the performance penalty of maintaining statistics, some implementations may not record
     * the usage history immediately or at all.
     *
     * @return [QuinineCacheStats] snapshot instance
     */
    val stats: QuinineCacheStats

    /**
     * Returns the approximate number of entries in this cache. The value returned is an estimate; the
     * actual count may differ if there are concurrent insertions or removals, or if some entries are
     * pending removal due to expiration or weak/soft reference collection. In the case of stale
     * entries this inaccuracy can be mitigated by performing a [cleanUp] first.
     */
    val estimatedSize: Long

    /**
     * Discards any cached value for the [key]. The behavior of this operation is undefined for
     * an entry that is being loaded (or reloaded) and is otherwise not present.
     *
     * @param key the key whose mapping is to be removed from the cache
     */
    fun invalidate(key: Any)

    /**
     * Discards any cached values for the [keys]. The behavior of this operation is undefined
     * for an entry that is being loaded (or reloaded) and is otherwise not present.
     *
     * @param keys the keys whose associated values are to be removed
     */
    fun invalidateAll(keys: Iterable<K>)

    /**
     * Discards all entries in the cache. The behavior of this operation is undefined for an entry
     * that is being loaded (or reloaded) and is otherwise not present.
     */
    fun invalidateAll()


    /**
     * Associates the [value] with the [key] in this cache. If the cache previously contained
     * a value associated with the [key], the old value is replaced by the new [value]
     * <p>
     * Prefer [get] when using the conventional "if cached, return; otherwise create, cache and return" pattern.
     *
     * @param key the key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     */
    fun put(key: K, value: Single<V>)

    /**
     * Copies all of the mappings from the specified map to the cache. The effect of this call is
     * equivalent to that of calling [put] on this map once for each mapping from key
     * [K] to value [V] in the specified map. The behavior of this operation is undefined
     * if the specified map is modified while the operation is in progress.
     *
     * @param map the mappings to be stored in this cache
     */
    fun putAll(map: Map<out K, Single<V>>)

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
    fun get(key: K, mappingFunction: (K) -> Single<V>): Single<V>

    /**
     * Returns the value associated with the [key] in this cache, or <pre>null</pre> if there is no
     * cached value for the [key].
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or <pre>null</pre> if this cache contains
     *         no mapping for the key
     */
    fun getIfPresent(key: Any): Single<V>?

    /**
     * Returns a map of the values associated with the [keys] in this cache. The returned map
     * will only contain entries which are already present in the cache.
     * <p>
     * Note that duplicate elements in [keys], as determined by [Any.equals] , will be ignored.
     *
     * @param keys the keys whose associated values are to be returned
     * @return the readonly mapping of keys to values for the specified keys found in this cache
     */
    fun getAllPresent(keys: Iterable<K>): Map<K, Single<V>>

    fun subscribeInvalidate(channel: Observable<K>)
    fun <T : Any> subscribeInvalidate(channel: Observable<T>, transformer: (T) -> K)
    fun <T : Any> subscribeUpdate(channel: Observable<T>, transformer: (T) -> Pair<K, V>)
    fun unsubscribe(channel: Observable<*>)

    /**
     * Performs any pending maintenance operations needed by the cache. Exactly which activities are
     * performed -- if any -- is implementation-dependent.
     */
    fun cleanUp()
}