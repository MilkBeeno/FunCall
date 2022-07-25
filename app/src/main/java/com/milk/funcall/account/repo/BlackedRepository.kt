package com.milk.funcall.account.repo

import com.milk.funcall.account.api.ApiService
import com.milk.funcall.common.net.retrofit

class BlackedRepository {
    suspend fun getBlackedList(index: Int) = retrofit {
        ApiService.blackApiService.getBlackedList(index)
    }
}