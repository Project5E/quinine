package com.moumoux.quinine

import com.github.benmanes.caffeine.cache.Caffeine
import io.reactivex.Single

import com.moumoux.quinine.reactive.QuinineCache as rxQuinineCache
import com.moumoux.quinine.reactive.QuinineLocalCache as rxQuinineLocalCache

class Quinine<K, V> private constructor() {
    private val caffeine = Caffeine.newBuilder()

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

    fun <K1 : K?, V1 : V?> build(mappingFunction: (K1) -> V1): QuinineCache<K1, V1> {
        return QuinineLocalLoadingCache(caffeine.build() {
            Single.create<V1> { emitter -> emitter.onSuccess(mappingFunction(it)) }
        })
    }

    fun <K1 : K?, V1 : V?> rxBuild(): rxQuinineCache<K1, V1> {
        return rxQuinineLocalCache(caffeine.build())
    }

}