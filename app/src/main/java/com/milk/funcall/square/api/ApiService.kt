package com.milk.funcall.square.api

import com.milk.funcall.common.net.ApiClient

object ApiService {
    val squareApiService: SquareApiService =
        ApiClient.getMainRetrofit().create(SquareApiService::class.java)
}