package com.milk.funcall.login.repo

import com.milk.funcall.common.author.AuthType
import com.milk.funcall.common.net.retrofit
import com.milk.funcall.login.api.ApiService

class LoginRepository {
   internal suspend fun login(deviceNum: String, authType: AuthType, accessToken: String) = retrofit {
        ApiService.loginApiService.login(deviceNum, authType.value, accessToken)
    }
}