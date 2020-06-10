package com.moumoux.quinine.common

import com.moumoux.quinine.CacheStats


interface BaseCache<K: Any, V> {
    /**
     * Returns a current snapshot of this cache's cumulative statistics. All statistics are
     * initialized to zero, and are monotonically increasing over the lifetime of the cache.
     * <p>
     * Due to the performance penalty of maintaining statistics, some implementations may not record
     * the usage history immediately or at all.
     *
     * @return [CacheStats] snapshot instance
     */
    val stats: CacheStats

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
    fun invalidate(key: K)

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
     * Performs any pending maintenance operations needed by the cache. Exactly which activities are
     * performed -- if any -- is implementation-dependent.
     */
    fun cleanUp()
}

