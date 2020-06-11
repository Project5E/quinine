package com.moumoux.quinine

import com.github.benmanes.caffeine.cache.Caffeine
import com.moumoux.quinine.suspend.Cache
import com.moumoux.quinine.suspend.LoadingCache
import com.moumoux.quinine.suspend.impl.CacheDelegate
import com.moumoux.quinine.suspend.impl.LoadingCacheDelegate
import io.reactivex.Single
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

import com.moumoux.quinine.reactive.Cache as rxCache
import com.moumoux.quinine.reactive.LoadingCache as rxLoadingCache
import com.moumoux.quinine.reactive.impl.CacheDelegate as rxCacheDelegate
import com.moumoux.quinine.reactive.impl.LoadingCacheDelegate as rxLoadingCacheDelegate

/**
 * A builder for [Cache], [LoadingCache], [rxCache], [rxLoadingCacheDelegate].
 * LoadingCache can automatic load entries into the cache, typically asynchronously.
 * By default, [Quinine] enables the stats recording which can be access through [Cache.stats].
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
 * So far, the backend of [Cache] is Caffeine which might changes in future.
 *
 * @author light.tsing@gmail.com (Akase Cho)
 * @param K the most general key type this builder will be able to create caches for.
 * @param V the most general value type this builder will be able to create caches for.
 */
class Quinine<K: Any, V> private constructor() {
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
     * @return [Cache] instance
     */
    fun <K1 : K, V1 : V?> build(): Cache<K1, V1> {
        return CacheDelegate(caffeine.build())
    }

    /**
     * Build a coroutine style cache with a loader
     *
     * @return [LoadingCache] instance
     */
    fun <K1 : K, V1 : V?> build(mappingFunction: suspend (K1) -> V1): LoadingCache<K1, V1> {
        return LoadingCacheDelegate(caffeine.build {
            Single.create<V1> { emitter ->
                GlobalScope.launch {
                    try {
                        emitter.onSuccess(mappingFunction(it))
                    } catch (e: Throwable) {
                        emitter.onError(e)
                    }
                }
            }.cache()
        })
    }

    /**
     * Build a reactive style cache
     *
     * @return [rxCache] instance
     */
    fun <K1 : K, V1 : V?> rxBuild(): rxCache<K1, V1> {
        return rxCacheDelegate(caffeine.build())
    }

    /**
     * Build a reactive style cache with a loader
     *
     * @return [rxLoadingCache] instance
     */
    fun <K1 : K, V1 : V?> rxBuild(mappingFunction: (K1) -> Single<V1>): rxLoadingCache<K1, V1> {
        return rxLoadingCacheDelegate(caffeine.build(mappingFunction))
    }

}