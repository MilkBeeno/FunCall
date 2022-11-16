package com.milk.funcall.app

import com.milk.funcall.BaseApplication
import com.milk.funcall.common.net.ApiClient
import com.milk.funcall.common.net.retrofit

class AppRepository {
    private val appService = ApiClient.getMainRetrofit().create(AppService::class.java)

    internal suspend fun getConfig(appId: String, pkgVersion: String, channel: String) = retrofit {
        appService.getConfig(appId, pkgVersion, channel)
    }

    internal suspend fun getSubscribeStatus(productId: String, purchaseToken: String) = retrofit {
        appService.getSubscribeStatus(
            BaseApplication.instance.packageName,
            productId,
            purchaseToken
        )
    }
}