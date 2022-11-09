package com.milk.funcall.user.api

import com.milk.funcall.common.net.ApiClient

object ApiService {
    internal val homeApiService: HomeApiService =
        ApiClient.obtainRetrofit().create(HomeApiService::class.java)
    internal val userInfoApiService: UserInfoApiService =
        ApiClient.obtainRetrofit().create(UserInfoApiService::class.java)
}