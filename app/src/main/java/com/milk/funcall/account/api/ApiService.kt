package com.milk.funcall.account.api

import com.milk.funcall.common.net.ApiClient

object ApiService {
    val fansApiService: FansApiService =
        ApiClient.obtainRetrofit().create(FansApiService::class.java)
}