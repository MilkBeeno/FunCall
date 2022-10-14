package com.milk.funcall.account.repo

import com.milk.funcall.account.api.FansOrFollowsApiService
import com.milk.funcall.common.net.ApiClient
import com.milk.funcall.common.net.retrofit

class FansOrFollowsRepository {
    private val fansOrFollowsApiService =
            ApiClient.obtainRetrofit().create(FansOrFollowsApiService::class.java)

    suspend fun getFans(pageIndex: Int) = retrofit {
        fansOrFollowsApiService.getFans(pageIndex)
    }

    suspend fun getFollows(pageIndex: Int) = retrofit {
        fansOrFollowsApiService.getFollows(pageIndex)
    }
}