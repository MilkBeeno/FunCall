package com.milk.funcall.app

import com.milk.funcall.common.net.ApiClient
import com.milk.funcall.common.net.retrofit

class AppRepository {
    private val appService = ApiClient.obtainRetrofit().create(AppService::class.java)

    suspend fun getConfig(appId: String, pkgVersion: String, channel: String) = retrofit {
        appService.getConfig(appId, pkgVersion, channel)
    }
}