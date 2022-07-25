package com.milk.funcall.account.api

import com.milk.funcall.common.net.ApiClient

object ApiService {
    val fansOrFollowsApiService: FansOrFollowsApiService =
        ApiClient.obtainRetrofit().create(FansOrFollowsApiService::class.java)
    val editProfileApiService: EditProfileApiService =
        ApiClient.obtainRetrofit().create(EditProfileApiService::class.java)
    val accountApiService: AccountApiService =
        ApiClient.obtainRetrofit().create(AccountApiService::class.java)
    val blackApiService: BlackedApiService =
        ApiClient.obtainRetrofit().create(BlackedApiService::class.java)
}