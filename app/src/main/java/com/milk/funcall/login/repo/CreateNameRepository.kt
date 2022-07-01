package com.milk.funcall.login.repo

import com.milk.funcall.common.net.retrofit
import com.milk.funcall.login.api.ApiService
import com.milk.funcall.user.type.Gender

class CreateNameRepository {
    suspend fun getUserAvatarName(gender: Gender) = retrofit {
        ApiService.loginApiService.getUserAvatarName(gender.value)
    }
}