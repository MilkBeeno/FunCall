package com.milk.funcall.login.api

import com.milk.funcall.common.net.ApiClient

object ApiService {
    internal val loginApiService: LoginApiService =
        ApiClient.obtainRetrofit().create(LoginApiService::class.java)
}