package com.milk.funcall.account.repo

import com.milk.funcall.account.api.BlackedApiService
import com.milk.funcall.common.net.ApiClient
import com.milk.funcall.common.net.retrofit

class BlackedRepository {
    private val blackApiService =
        ApiClient.getMainRetrofit().create(BlackedApiService::class.java)

    internal suspend fun getBlackedList(index: Int) = retrofit {
        blackApiService.getBlackedList(index)
    }
}