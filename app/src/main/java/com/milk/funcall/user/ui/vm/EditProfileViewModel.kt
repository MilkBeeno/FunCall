package com.milk.funcall.user.ui.vm

import androidx.lifecycle.ViewModel
import com.milk.funcall.account.Account
import com.milk.funcall.common.media.uploader.MediaUploadRepository
import com.milk.simple.ktx.ioScope

class EditProfileViewModel : ViewModel() {
    private val mediaUploadRepository by lazy { MediaUploadRepository() }
    var localAvatarPath: String = ""
    val localImageListPath = mutableListOf<String>()

    fun uploadProfile(name: String, bio: String) {
        ioScope {
            val mediaProfile = uploadMediaProfile()
        }
    }

    private suspend fun uploadMediaProfile(): MutableList<String> {
        val filePathList = mutableListOf<String>()
        if (localAvatarPath.isNotBlank())
            filePathList.add(localAvatarPath)
        if (localImageListPath.isNotEmpty())
            localImageListPath.forEach { cache ->
                if (!Account.userImageList.contains(cache))
                    filePathList.add(cache)
            }
        if (filePathList.isNotEmpty()) {
            val apiResponse =
                mediaUploadRepository.uploadMultiplePicture(filePathList)
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null)
                return apiResult
        }
        return mutableListOf()
    }
}