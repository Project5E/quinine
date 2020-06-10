package com.moumoux.quinine.common

import io.reactivex.Observable
import java.lang.RuntimeException

interface NotifyUpdateCache<K: Any, V>: NotifyCache<K, V> {
    fun subscribeUpdate(channel: Observable<K>) {
        throw RuntimeException("No loading function was given")
    }
    fun <T : Any> subscribeUpdate(channel: Observable<T>, transformer: (T) -> Pair<K, V>)
}