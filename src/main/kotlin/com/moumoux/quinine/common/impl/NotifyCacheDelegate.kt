package com.moumoux.quinine.common.impl

import com.moumoux.quinine.common.BaseCache
import com.moumoux.quinine.common.NotifyCache
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.ConcurrentHashMap

internal class NotifyCacheDelegate<K : Any, V>(internal val cache: BaseCache<K, V>): BaseCache<K, V> by cache, NotifyCache<K, V> {
    override val subscriptions: MutableMap<Observable<*>, Disposable> = ConcurrentHashMap()

    override fun subscribeInvalidate(channel: Observable<K>) {
        subscriptions[channel] = channel.subscribe { invalidate(it) }
    }

    override fun <T : Any> subscribeInvalidate(channel: Observable<T>, transformer: (T) -> K) {
        subscriptions[channel] = channel.subscribe {
            invalidate(transformer(it))
        }
    }

    override fun unsubscribe(channel: Observable<*>) {
        subscriptions[channel]?.dispose()
        subscriptions.remove(channel)
    }
}