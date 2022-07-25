package com.milk.funcall.user.repo

import com.milk.funcall.common.mdr.DataBaseManager
import com.milk.funcall.common.mdr.table.UserInfoEntity
import com.milk.funcall.common.net.retrofit
import kotlinx.coroutines.flow.Flow

object UserInfoRepository {
    internal val userTotalInfoRepository by lazy { UserTotalInfoRepository() }

    internal fun getUserInfoByDB(targetId: Long): Flow<UserInfoEntity?> {
        return DataBaseManager.DB.userInfoTableDao().query(targetId)
    }

    internal suspend fun getUserInfoByNetwork(targetId: Long) = retrofit {
        userTotalInfoRepository.getUserTotalInfo(targetId)
    }
}