package com.milk.funcall.user.repo

import com.milk.funcall.account.Account
import com.milk.funcall.common.net.retrofit
import com.milk.funcall.user.api.ApiService

class HomeRepository {
    suspend fun getHomeList(pageIndex: Int, groupNumber: Int) = retrofit {
        ApiService.homeApiService.getHomeList(pageIndex, Account.gender.value, groupNumber)
    }
}