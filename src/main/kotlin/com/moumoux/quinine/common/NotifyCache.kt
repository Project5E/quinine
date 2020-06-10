package com.moumoux.quinine.common

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

interface NotifyCache<K : Any, V> {
    val subscriptions: MutableMap<Observable<*>, Disposable>

    fun subscribeInvalidate(channel: Observable<K>)
    fun <T : Any> subscribeInvalidate(channel: Observable<T>, transformer: (T) -> K)
    fun unsubscribe(channel: Observable<*>)
}