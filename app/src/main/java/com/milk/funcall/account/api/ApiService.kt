package com.milk.funcall.account.api

import com.milk.funcall.common.net.ApiClient

object ApiService {
    val fansOrFollowsApiService: FansOrFollowsApiService =
        ApiClient.obtainRetrofit().create(FansOrFollowsApiService::class.java)
}