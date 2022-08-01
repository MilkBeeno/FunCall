package com.milk.funcall.ad.repo

import com.milk.funcall.ad.api.ApiService
import com.milk.funcall.common.net.retrofit

class AdRepository {
    suspend fun getAdSwitch(appId: String, pkgVersion: String, channel: String) = retrofit {
        ApiService.adApiService.getAdSwitch(appId, pkgVersion, channel)
    }

    suspend fun getAdConfig(appId: String, pkgVersion: String, channel: String) = retrofit {
        ApiService.adApiService.getAdConfig(appId, pkgVersion, channel)
    }
}