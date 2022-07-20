package com.milk.funcall.chat.api

import com.milk.funcall.common.net.ApiClient

object ApiService {
    val chatApiService: ChatApiService =
        ApiClient.obtainRetrofit().create(ChatApiService::class.java)
}