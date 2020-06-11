package com.moumoux.quinine.suspend.impl

import com.github.benmanes.caffeine.cache.Cache as CaffeineCache
import com.moumoux.quinine.common.BaseCache
import com.moumoux.quinine.common.NotifyCache
import com.moumoux.quinine.common.NotifyUpdateCache
import com.moumoux.quinine.common.impl.BaseCacheDelegate
import com.moumoux.quinine.common.impl.NotifyCacheDelegate
import com.moumoux.quinine.suspend.Cache
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await

open class CacheDelegate<K : Any, V>(
    internal open val cache: CaffeineCache<K, Single<V>>,
    private val baseCacheDelegate: BaseCache<K, V> = BaseCacheDelegate(cache),
    internal val notifyCacheDelegate: NotifyCache<K, V> = NotifyCacheDelegate(baseCacheDelegate)
) : BaseCache<K, V> by baseCacheDelegate,
    NotifyCache<K, V> by notifyCacheDelegate,
    NotifyUpdateCache<K, V>,
    Cache<K, V> {

    override fun put(key: K, value: V) = cache.put(key, Single.just(value).cache())
    override fun putAll(map: Map<out K, V>) {
        map.forEach { (k, u) -> cache.put(k, Single.just(u).cache()) }
    }

    override suspend fun get(key: K, mappingFunction: suspend (K) -> V): V {
        return coroutineScope {
            cache.get(key) {
                Single.create<V> { emitter ->
                    launch {
                        try {
                            emitter.onSuccess(mappingFunction(it))
                        } catch (e: Throwable) {
                            emitter.onError(e)
                        }
                    }
                }.cache()
            }!!.await()

        }
    }

    override suspend fun getIfPresent(key: K): V? = cache.getIfPresent(key)?.await()
    override suspend fun getAllPresent(keys: Iterable<K>): Map<K, V> =
        cache.getAllPresent(keys).mapValues { it.value.await() }

    override fun <T : Any> subscribeUpdate(channel: Observable<T>, transformer: (T) -> Pair<K, V>) {
        subscriptions[channel] = channel.subscribe {
            val (k, v) = transformer(it)
            put(k, v)
        }
    }

}