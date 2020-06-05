package com.moumoux.quinine

import com.github.benmanes.caffeine.cache.Caffeine
import io.reactivex.Single

import com.moumoux.quinine.reactive.QuinineCache as rxQuinineCache
import com.moumoux.quinine.reactive.QuinineCache as rxQuinineLoadingCache
import com.moumoux.quinine.reactive.QuinineLocalCache as rxQuinineLocalCache
import com.moumoux.quinine.reactive.QuinineLocalLoadingCache as rxQuinineLocalLoadingCache

/**
 * A builder for [QuinineCache], [QuinineLoadingCache], [rxQuinineCache], [rxQuinineLoadingCache].
 * LoadingCache can automatic load entries into the cache, typically asynchronously.
 * By default, [Quinine] enables the stats recording which can be access through [QuinineCache.stats].
 * Usage example:
 * <pre>
 *     val cache: QuinineCache<Int, Int> = Quinine
 *         .newBuilder()
 *         .maximumSize(500)
 *         .build()
 * </pre>
 * LoadingCache example:
 * <pre>
 *     val cache: QuinineLoadingCache<Int, Int> = Quinine
 *         .newBuilder()
 *         .maximumSize(500)
 *         .build { load(it) }
 * </pre>
 *
 * So far, the backend of [QuinineCache] is Caffeine which might changes in future.
 *
 * @author light.tsing@gmail.com (Akase Cho)
 * @param K the most general key type this builder will be able to create caches for.
 * @param V the most general value type this builder will be able to create caches for.
 */
class Quinine<K, V> private constructor() {
    private val caffeine = Caffeine.newBuilder().recordStats()

    companion object {
        /**
         * Create a new [Quinine] builder
         * @return [Quinine] instance
         */
        @JvmStatic
        fun newBuilder() = Quinine<Any, Any>()
    }

    /**
     * Set the maximum size of the size
     *
     * @param maximumSize maximum size
     * @return [Quinine] then it can be fluent
     */
    fun maximumSize(maximumSize: Long): Quinine<K, V> {
        caffeine.maximumSize(maximumSize)
        return this
    }

    /**
     * Build a coroutine style cache
     *
     * @return [QuinineCache] instance
     */
    fun <K1 : K?, V1 : V?> build(): QuinineCache<K1, V1> {
        return QuinineLocalCache(caffeine.build())
    }

    /**
     * Build a coroutine style cache with a loader
     *
     * @return [QuinineLoadingCache] instance
     */
    fun <K1 : K?, V1 : V?> build(mappingFunction: (K1) -> V1): QuinineLoadingCache<K1, V1> {
        return QuinineLocalLoadingCache(caffeine.build {
            Single.create { emitter -> emitter.onSuccess(mappingFunction(it)) }
        })
    }

    /**
     * Build a reactive style cache
     *
     * @return [rxQuinineCache] instance
     */
    fun <K1 : K?, V1 : V?> rxBuild(): rxQuinineCache<K1, V1> {
        return rxQuinineLocalCache(caffeine.build())
    }

    /**
     * Build a reactive style cache with a loader
     *
     * @return [rxQuinineLoadingCache] instance
     */
    fun <K1 : K?, V1 : V?> rxBuild(mappingFunction: (K1) -> Single<V1>): rxQuinineLoadingCache<K1, V1> {
        return rxQuinineLocalLoadingCache(caffeine.build(mappingFunction))
    }

}