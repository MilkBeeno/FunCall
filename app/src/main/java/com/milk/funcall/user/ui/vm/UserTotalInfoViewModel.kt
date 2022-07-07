package com.milk.funcall.user.ui.vm

import androidx.lifecycle.ViewModel
import com.milk.funcall.user.data.UserTotalInfoModel
import com.milk.funcall.user.repo.UserTotalInfoRepository
import com.milk.simple.ktx.ioScope
import kotlinx.coroutines.flow.MutableSharedFlow

class UserTotalInfoViewModel : ViewModel() {
    private val userTotalInfoRepository by lazy { UserTotalInfoRepository() }
    val dataRequestStateFlow = MutableSharedFlow<Boolean>()
    val userTotalInfoFlow = MutableSharedFlow<UserTotalInfoModel>()
    val userFollowedChangeFlow = MutableSharedFlow<Boolean>()

    fun getUserTotalInfo(userId: Long) {
        ioScope {
            val apiResponse = if (userId > 0)
                userTotalInfoRepository.getUserTotalInfo(userId)
            else
                userTotalInfoRepository.getNextUserTotalInfo()
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                dataRequestStateFlow.emit(true)
                userTotalInfoFlow.emit(apiResult)
            } else dataRequestStateFlow.emit(false)
        }
    }
}