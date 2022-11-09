package com.milk.funcall.user.repo

import com.milk.funcall.account.Account
import com.milk.funcall.app.AppConfig
import com.milk.funcall.common.db.DataBaseManager
import com.milk.funcall.common.db.table.UserInfoEntity
import com.milk.funcall.common.net.retrofit
import com.milk.funcall.user.api.ApiService
import com.milk.funcall.user.data.UserInfoModel
import kotlinx.coroutines.flow.Flow

object UserInfoRepository {

    internal fun getUserInfoByDB(targetId: Long): Flow<UserInfoEntity?> {
        return DataBaseManager.DB.userInfoTableDao().query(targetId)
    }

    internal suspend fun getUserInfoByNetwork(userId: Long) = retrofit {
        val apiResponse =
            ApiService.userInfoApiService.getUserInfoByNetwork(userId)
        val apiResult = apiResponse.data
        if (apiResponse.success && apiResult != null && Account.userLogged)
            saveUserInfoToDB(apiResult)
        apiResponse
    }

    internal suspend fun getNextUserInfoByNetwork() = retrofit {
        val apiResponse =
            ApiService.userInfoApiService.getNextUserInfoByNetwork(Account.userGender)
        val apiResult = apiResponse.data
        if (apiResponse.success && apiResult != null && Account.userLogged)
            saveUserInfoToDB(apiResult)
        apiResponse
    }

    private fun saveUserInfoToDB(userInfo: UserInfoModel) {
        val userInfoEntity = UserInfoEntity()
        userInfoEntity.accountId = Account.userId
        userInfoEntity.targetId = userInfo.targetId
        userInfoEntity.targetName = userInfo.targetName
        userInfoEntity.targetAvatar = userInfo.targetAvatar
        userInfoEntity.targetGender = userInfo.targetGender
        userInfoEntity.targetImage = userInfo.targetImage
        userInfoEntity.targetVideo = userInfo.targetVideo
        userInfoEntity.targetOnline = userInfo.targetOnline
        userInfoEntity.targetIsFollowed = userInfo.targetIsFollowed
        userInfoEntity.targetIsBlacked = userInfo.targetIsBlacked
        DataBaseManager.DB.userInfoTableDao().insert(userInfoEntity)
    }

    internal suspend fun getUnlockInfo(deviceNumber: String, targetId: Long) =
        retrofit {
            ApiService.userInfoApiService.getUnlockInfo(
                deviceNumber,
                AppConfig.freeUnlockTimes,
                AppConfig.viewAdUnlockTimes,
                targetId
            )
        }

    internal suspend fun changeFollowedStatus(targetId: Long, isFollowed: Boolean) = retrofit {
        val apiResponse =
            ApiService.userInfoApiService.changeFollowedStatus(targetId, isFollowed)
        if (apiResponse.success) {
            // 更新本地关注数量
            val lastFollows = Account.userFollows
            Account.userFollows =
                if (isFollowed) lastFollows + 1 else lastFollows - 1
            Account.userFollowsFlow.emit(Account.userFollows)
            // 更新数据库中对当前用户的状态
            DataBaseManager.DB.userInfoTableDao()
                .updateFollowedStatus(Account.userId, targetId, isFollowed)
        }
        apiResponse
    }

    internal suspend fun blackUser(targetId: Long) = retrofit {
        ApiService.userInfoApiService.blackUser(targetId)
    }

    internal suspend fun changeUnlockStatus(deviceId: String, unlockType: Int, unlockUserId: Long) =
        retrofit {
            ApiService.userInfoApiService.changeUnlockStatus(deviceId, unlockType, unlockUserId)
        }
}