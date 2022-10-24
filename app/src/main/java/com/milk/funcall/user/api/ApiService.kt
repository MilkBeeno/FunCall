package com.milk.funcall.user.api

import com.milk.funcall.common.net.ApiClient

object ApiService {
    val homeApiService: HomeApiService =
        ApiClient.obtainRetrofit().create(HomeApiService::class.java)
    val userInfoApiService: UserInfoApiService =
        ApiClient.obtainRetrofit().create(UserInfoApiService::class.java)
}