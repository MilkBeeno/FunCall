package com.milk.funcall.user.ui.vm

import androidx.lifecycle.ViewModel
import com.milk.funcall.account.Account
import com.milk.funcall.user.data.UserTotalInfoModel
import com.milk.funcall.user.repo.UserTotalInfoRepository
import com.milk.simple.ktx.ioScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class UserTotalInfoViewModel : ViewModel() {
    private val userTotalInfoRepository by lazy { UserTotalInfoRepository() }
    val userTotalInfoFlow = MutableStateFlow<UserTotalInfoModel?>(UserTotalInfoModel())
    val userFollowedChangeFlow = MutableSharedFlow<Boolean?>()

    fun getUserTotalInfo(userId: Long) {
        ioScope {
            val apiResponse = if (userId > 0)
                userTotalInfoRepository.getUserTotalInfo(userId)
            else
                userTotalInfoRepository.getNextUserTotalInfo()
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                apiResult.mediaConvert()
                userTotalInfoFlow.emit(apiResult)
            } else userTotalInfoFlow.emit(null)
        }
    }

    fun changeFollowState() {
        ioScope {
            val targetId = userTotalInfoFlow.value?.userId ?: 0
            val isFollow = !(userTotalInfoFlow.value?.isFollowed ?: false)
            val apiResponse =
                userTotalInfoRepository.changeFollowState(targetId, isFollow)
            if (apiResponse.success) {
                userTotalInfoFlow.value?.isFollowed = isFollow
                userFollowedChangeFlow.emit(isFollow)
                // 更新本地粉丝数量
                val lastFollows = Account.userFollows
                Account.userFollows =
                    if (isFollow) lastFollows + 1 else lastFollows - 1
                Account.userFollowsFlow.emit(Account.userFollows)
            } else userFollowedChangeFlow.emit(null)
        }
    }
}