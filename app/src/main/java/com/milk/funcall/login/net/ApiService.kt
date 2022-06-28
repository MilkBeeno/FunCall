package com.milk.funcall.login.net

import com.milk.funcall.common.net.ApiClient

object ApiService {
    val loginApiService: LoginApiService =
        ApiClient.obtainRetrofit().create(LoginApiService::class.java)
}