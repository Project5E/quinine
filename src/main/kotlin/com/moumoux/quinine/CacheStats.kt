package com.moumoux.quinine

import com.github.benmanes.caffeine.cache.stats.CacheStats as CaffeineStats

data class CacheStats(val hitCount: Long, val missCount: Long) {

    val hitRate: Double
        get() = hitCount.toDouble() / (missCount + hitCount).toDouble()
    val missRate: Double
        get() = missCount.toDouble() / (missCount + hitCount).toDouble()

    companion object {
        @JvmStatic
        fun from(stats: CaffeineStats): CacheStats {
            return CacheStats(stats.hitCount(), stats.missCount())
        }
    }
}