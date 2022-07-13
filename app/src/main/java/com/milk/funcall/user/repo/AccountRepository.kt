package com.milk.funcall.user.repo

import com.milk.funcall.account.Account
import com.milk.funcall.common.net.retrofit
import com.milk.funcall.login.api.ApiService
import com.milk.simple.ktx.ioScope

object AccountRepository {
    fun getAccountInfo(registered: Boolean) {
        ioScope {
            val apiResponse = retrofit { ApiService.loginApiService.getUserInfo() }
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null)
                Account.saveAccountInfo(apiResult, registered)
        }
    }
}