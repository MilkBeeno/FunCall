package com.milk.funcall.user.api

import com.milk.funcall.common.net.ApiClient

object ApiService {
    internal val homeApiService: HomeApiService =
        ApiClient.getMainRetrofit().create(HomeApiService::class.java)
    internal val userInfoApiService: UserInfoApiService =
        ApiClient.getMainRetrofit().create(UserInfoApiService::class.java)
}