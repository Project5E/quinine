package com.moumoux.quinine

import com.github.benmanes.caffeine.cache.Caffeine
import io.reactivex.Single

import com.moumoux.quinine.reactive.QuinineCache as rxQuinineCache
import com.moumoux.quinine.reactive.QuinineCache as rxQuinineLoadingCache
import com.moumoux.quinine.reactive.QuinineLocalCache as rxQuinineLocalCache
import com.moumoux.quinine.reactive.QuinineLocalCache as rxQuinineLocalLoadingCache

class Quinine<K, V> private constructor() {
    private val caffeine = Caffeine.newBuilder().recordStats()

    companion object {
        fun newBuilder() = Quinine<Any, Any>()
    }

    fun maximumSize(maximumSize: Long): Quinine<K, V> {
        caffeine.maximumSize(maximumSize)
        return this
    }

    fun <K1 : K?, V1 : V?> build(): QuinineCache<K1, V1> {
        return QuinineLocalCache(caffeine.build())
    }

    fun <K1 : K?, V1 : V?> build(mappingFunction: (K1) -> V1): QuinineLoadingCache<K1, V1> {
        return QuinineLocalLoadingCache(caffeine.build {
            Single.create { emitter -> emitter.onSuccess(mappingFunction(it)) }
        })
    }

    fun <K1 : K?, V1 : V?> rxBuild(): rxQuinineCache<K1, V1> {
        return rxQuinineLocalCache(caffeine.build())
    }

    fun <K1 : K?, V1 : V?> rxBuild(mappingFunction: (K1) -> Single<V1>): rxQuinineLoadingCache<K1, V1> {
        return rxQuinineLocalLoadingCache(caffeine.build(mappingFunction))
    }

}