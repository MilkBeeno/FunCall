package com.milk.funcall.login.repo

import com.milk.funcall.account.Account
import com.milk.funcall.common.net.retrofit
import com.milk.funcall.login.api.ApiService

class PresetProfileRepository {
    internal suspend fun getUserAvatarName(gender: String) = retrofit {
        ApiService.loginApiService.getUserAvatarName(gender)
    }

    internal suspend fun updateUserProfile(name: String, imageUrl: String) = retrofit {
        ApiService.loginApiService.updateUserProfile(name, imageUrl, Account.userGender)
    }
}