package com.milk.funcall.common.net.host

import com.milk.funcall.BuildConfig

interface ApiHost {
    val realUrl: String
        get() = if (BuildConfig.DEBUG) debugUrl() else releaseUrl()

    fun releaseUrl(): String
    fun debugUrl(): String
}