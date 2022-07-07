package com.milk.funcall.user.api

import com.milk.funcall.common.net.ApiClient

object ApiService {
    val homeApiService: HomeApiService =
        ApiClient.obtainRetrofit().create(HomeApiService::class.java)
    val userTotalInfApiService: UserTotalInfoApiService =
        ApiClient.obtainRetrofit().create(UserTotalInfoApiService::class.java)
}