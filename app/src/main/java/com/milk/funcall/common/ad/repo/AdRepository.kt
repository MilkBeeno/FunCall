package com.milk.funcall.common.ad.repo

import com.milk.funcall.common.ad.api.AdApiService
import com.milk.funcall.common.net.ApiClient
import com.milk.funcall.common.net.retrofit

class AdRepository {
    private val adApiService: AdApiService =
        ApiClient.getMainRetrofit().create(AdApiService::class.java)

    internal suspend fun getAdConfig(appId: String, pkgVersion: String, channel: String) = retrofit {
        adApiService.getAdConfig(appId, pkgVersion, channel)
    }
}