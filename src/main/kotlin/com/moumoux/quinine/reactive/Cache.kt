package com.moumoux.quinine.reactive

import com.moumoux.quinine.common.BaseCache
import com.moumoux.quinine.common.GetCache
import com.moumoux.quinine.common.PutCache
import io.reactivex.Single

interface Cache<K: Any, V>: BaseCache<K, V>, GetCache<K, Single<V>>, PutCache<K, Single<V>>