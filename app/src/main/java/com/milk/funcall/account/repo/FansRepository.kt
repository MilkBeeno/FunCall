package com.milk.funcall.account.repo

import com.milk.funcall.account.api.ApiService
import com.milk.funcall.common.net.retrofit

class FansRepository {
    suspend fun getFans(pageIndex: Int) = retrofit {
        ApiService.fansApiService.getFans(pageIndex)
    }
}