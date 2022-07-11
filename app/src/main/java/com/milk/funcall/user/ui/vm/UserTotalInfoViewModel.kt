package com.milk.funcall.user.ui.vm

import androidx.lifecycle.ViewModel
import com.milk.funcall.user.data.UserMediaModel
import com.milk.funcall.user.data.UserTotalInfoModel
import com.milk.funcall.user.repo.UserTotalInfoRepository
import com.milk.funcall.user.type.Material
import com.milk.simple.ktx.ioScope
import kotlinx.coroutines.flow.MutableSharedFlow

class UserTotalInfoViewModel : ViewModel() {
    private val userTotalInfoRepository by lazy { UserTotalInfoRepository() }
    private val userVideoList = mutableListOf<UserMediaModel>()
    private val userImageList = mutableListOf<UserMediaModel>()
    val userTotalInfoFlow = MutableSharedFlow<UserTotalInfoModel?>()
    val userFollowedChangeFlow = MutableSharedFlow<Boolean>()

    fun getUserTotalInfo(userId: Long) {
        ioScope {
            val apiResponse = if (userId > 0)
                userTotalInfoRepository.getUserTotalInfo(userId)
            else
                userTotalInfoRepository.getNextUserTotalInfo()
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                apiResult.userMaterialList?.forEach {
                    val isVideo = it.materialType == Material.Video.value
                    if (isVideo) userVideoList.add(it) else userImageList.add(it)
                }
                apiResult.userVideoList = userVideoList
                apiResult.userImageList = userImageList
                userTotalInfoFlow.emit(apiResult)
            } else userTotalInfoFlow.emit(null)
        }
    }
}