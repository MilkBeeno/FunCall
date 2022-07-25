package com.milk.funcall.user.ui.vm

import androidx.lifecycle.ViewModel
import com.milk.funcall.user.data.UserInfoModel
import com.milk.funcall.user.repo.UserInfoRepository
import com.milk.simple.ktx.ioScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class UserInfoViewModel : ViewModel() {
    val userInfoFlow = MutableStateFlow<UserInfoModel?>(UserInfoModel())
    val userFollowedStatusFlow = MutableSharedFlow<Boolean?>()

    fun getUserTotalInfo(userId: Long) {
        ioScope {
            val apiResponse = if (userId > 0)
                UserInfoRepository.getUserInfoByNetwork(userId)
            else
                UserInfoRepository.getNextUserInfoByNetwork()
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null)
                userInfoFlow.emit(apiResult)
            else
                userInfoFlow.emit(null)
        }
    }

    fun changeFollowedStatus() {
        ioScope {
            val targetId = userInfoFlow.value?.targetId ?: 0
            val isFollowed = !(userInfoFlow.value?.targetIsFollowed ?: false)
            val apiResponse =
                UserInfoRepository.changeFollowedStatus(targetId, isFollowed)
            if (apiResponse.success) {
                userInfoFlow.value?.targetIsFollowed = isFollowed
                userFollowedStatusFlow.emit(isFollowed)
            } else userFollowedStatusFlow.emit(null)
        }
    }
}