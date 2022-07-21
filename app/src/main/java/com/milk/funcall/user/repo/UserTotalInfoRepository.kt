package com.milk.funcall.user.repo

import com.milk.funcall.account.Account
import com.milk.funcall.common.mdr.DataBaseManager
import com.milk.funcall.common.mdr.table.UserInfoEntity
import com.milk.funcall.common.net.retrofit
import com.milk.funcall.user.api.ApiService
import com.milk.funcall.user.data.UserTotalInfoModel

class UserTotalInfoRepository {
    suspend fun getUserTotalInfo(userId: Long) = retrofit {
        val apiResponse =
            ApiService.userTotalInfApiService.getUserTotalInfo(userId)
        val apiResult = apiResponse.data
        if (apiResponse.success && apiResult != null && Account.userLogged)
            saveUserToDB(apiResult)
        apiResponse
    }

    suspend fun getNextUserTotalInfo() = retrofit {
        val apiResponse =
            ApiService.userTotalInfApiService.getNextUserTotalInfo(Account.userGender)
        val apiResult = apiResponse.data
        if (apiResponse.success && apiResult != null && Account.userLogged)
            saveUserToDB(apiResult)
        apiResponse
    }

    suspend fun changeFollowState(targetId: Long, isFollow: Boolean) = retrofit {
        ApiService.userTotalInfApiService.changeFollowState(targetId, isFollow)
    }

    private fun saveUserToDB(userInfo: UserTotalInfoModel) {
        val userInfoEntity = UserInfoEntity()
        userInfoEntity.userId = Account.userId
        userInfoEntity.targetId = userInfo.targetId
        userInfoEntity.userName = userInfo.userName
        userInfoEntity.userAvatar = userInfo.userAvatar
        userInfoEntity.userGender = userInfo.userGender
        userInfoEntity.userImage = userInfo.userImage
        userInfoEntity.userVideo = userInfo.userVideo
        userInfoEntity.userOnline = userInfo.userOnline
        DataBaseManager.DB.userInfoTableDao().insert(userInfoEntity)
    }
}