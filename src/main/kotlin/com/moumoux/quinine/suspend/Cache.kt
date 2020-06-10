package com.moumoux.quinine.suspend

import com.moumoux.quinine.common.BaseCache
import com.moumoux.quinine.common.PutCache

interface Cache<K: Any, V>: BaseCache<K, V>, GetCache<K, V>, PutCache<K, V>