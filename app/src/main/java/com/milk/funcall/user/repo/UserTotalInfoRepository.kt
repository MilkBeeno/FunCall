package com.milk.funcall.user.repo

import com.milk.funcall.account.Account
import com.milk.funcall.common.net.retrofit
import com.milk.funcall.user.api.ApiService

class UserTotalInfoRepository {
    suspend fun getUserTotalInfo(userId: Long) = retrofit {
        ApiService.userTotalInfApiService.getUserTotalInfo(userId)
    }

    suspend fun getNextUserTotalInfo() = retrofit {
        ApiService.userTotalInfApiService.getNextUserTotalInfo(Account.userGender)
    }

    suspend fun changeFollowState(targetId: Long, isFollow: Boolean) = retrofit {
        ApiService.userTotalInfApiService.changeFollowState(targetId, isFollow)
    }
}