package com.milk.funcall.login.ui.vm

import androidx.lifecycle.ViewModel
import com.milk.funcall.account.Account
import com.milk.funcall.common.media.uploader.MediaUploadRepository
import com.milk.funcall.login.repo.PresetProfileRepository
import com.milk.funcall.user.repo.AccountRepository
import com.milk.simple.ktx.ioScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class PresetProfileViewModel : ViewModel() {
    private val presetProfileRepository by lazy { PresetProfileRepository() }
    private val mediaUploadRepository by lazy { MediaUploadRepository() }
    val avatar = MutableStateFlow("")
    val name = MutableStateFlow("")
    var localAvatarPath: String = ""
    var uploadAvatarUrl: String = ""
    val uploadImage = MutableSharedFlow<Boolean>()
    val presetProfile = MutableSharedFlow<Boolean>()

    fun getUserAvatarName() {
        ioScope {
            val apiResponse =
                presetProfileRepository.getUserAvatarName(Account.userGender)
            val apiResult = apiResponse.data
            avatar.emit(apiResult?.avatarUrl ?: "")
            name.emit(apiResult?.nickname ?: "")
        }
    }

    fun updateUserProfile(name: String) {
        if (localAvatarPath.isNotBlank()) ioScope {
            val apiResponse = mediaUploadRepository.uploadPicture(localAvatarPath)
            uploadAvatarUrl = apiResponse.data.toString()
            uploadImage.emit(apiResponse.success)
        } else presetProfile(name)
    }

    fun presetProfile(name: String) {
        val finalUrl = uploadAvatarUrl.ifBlank { avatar.value }
        ioScope {
            val apiResponse = presetProfileRepository.updateUserProfile(name, finalUrl)
            if (apiResponse.success) AccountRepository.getAccountInfo(false)
            presetProfile.emit(apiResponse.success)
        }
    }
}