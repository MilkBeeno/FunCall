package com.milk.funcall.login.repo

import com.milk.funcall.common.net.retrofit
import com.milk.funcall.login.api.ApiService

class CreateNameRepository {
    suspend fun getUserAvatarName(gender: String) = retrofit {
        ApiService.loginApiService.getUserAvatarName(gender)
    }
}