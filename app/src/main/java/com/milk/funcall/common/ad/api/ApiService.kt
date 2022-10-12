package com.milk.funcall.common.ad.api

import com.milk.funcall.common.net.ApiClient

object ApiService {
    val adApiService: AdApiService =
        ApiClient.obtainRetrofit().create(AdApiService::class.java)
}