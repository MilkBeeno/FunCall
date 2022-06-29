package com.milk.funcall.login.repo

import com.milk.funcall.common.author.AuthType
import com.milk.funcall.common.net.retrofitCatch
import com.milk.funcall.login.net.ApiService

object LoginRepository {
    suspend fun login(deviceNum: String, authType: AuthType, accessToken: String) = retrofitCatch {
        ApiService.loginApiService.login(deviceNum, authType.value, accessToken)
    }
}