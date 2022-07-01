package com.milk.funcall.login.repo

import com.milk.funcall.common.net.retrofitCatch
import com.milk.funcall.login.net.ApiService
import com.milk.funcall.user.type.Gender

class CreateNameRepository {
    suspend fun obtainUserDefault(gender: Gender) = retrofitCatch {
        ApiService.loginApiService.obtainUserDefault(gender.value)
    }
}