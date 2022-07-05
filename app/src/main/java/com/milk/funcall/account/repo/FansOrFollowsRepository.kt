package com.milk.funcall.account.repo

import com.milk.funcall.account.api.ApiService
import com.milk.funcall.common.net.retrofit

class FansOrFollowsRepository {
    suspend fun getFans(pageIndex: Int) = retrofit {
        ApiService.fansOrFollowsApiService.getFans(pageIndex)
    }

    suspend fun getFollows(pageIndex: Int) = retrofit {
        ApiService.fansOrFollowsApiService.getFollows(pageIndex)
    }
}