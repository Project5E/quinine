package com.moumoux.quinine

import com.github.benmanes.caffeine.cache.stats.CacheStats

data class QuinineCacheStats(val hitCount: Long, val missCount: Long) {

    val hitRate: Double
        get() = hitCount.toDouble() / (missCount + hitCount).toDouble()
    val missRate: Double
        get() = missCount.toDouble() / (missCount + hitCount).toDouble()

    companion object {
        @JvmStatic
        fun from(stats: CacheStats): QuinineCacheStats {
            return QuinineCacheStats(stats.hitCount(), stats.missCount())
        }
    }
}