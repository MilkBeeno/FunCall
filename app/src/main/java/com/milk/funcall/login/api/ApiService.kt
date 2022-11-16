package com.milk.funcall.login.api

import com.milk.funcall.common.net.ApiClient

object ApiService {
    internal val loginApiService: LoginApiService =
        ApiClient.getMainRetrofit().create(LoginApiService::class.java)
}