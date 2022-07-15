package com.milk.funcall.user.ui.vm

import androidx.lifecycle.ViewModel
import com.milk.funcall.account.Account
import com.milk.funcall.account.repo.EditProfileRepository
import com.milk.funcall.common.media.uploader.MediaUploadRepository
import com.milk.simple.ktx.ioScope
import kotlinx.coroutines.flow.MutableSharedFlow

class EditProfileViewModel : ViewModel() {
    private val mediaUploadRepository by lazy { MediaUploadRepository() }
    private val editProfileRepository by lazy { EditProfileRepository() }
    var localAvatarPath: String = ""
    val localImageListPath = mutableListOf<String>()
    var uploadResult = MutableSharedFlow<Boolean>()

    fun uploadProfile(name: String, bio: String, link: String) {
        ioScope {
            val mediaProfile = uploadMediaProfile()
            val avatarUrl = mediaProfile.first
            val imageList = mediaProfile.second
            var video = ""
            val apiResponse = editProfileRepository
                .uploadProfile(avatarUrl, name, bio, link, video, imageList)
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                uploadResult.emit(true)
                Account.saveAccountInfo(apiResult)
            } else uploadResult.emit(false)
        }
    }

    private suspend fun uploadMediaProfile(): Pair<String, ArrayList<String>> {
        val filePathList = mutableListOf<String>()
        if (localAvatarPath.isNotBlank()) {
            filePathList.add(localAvatarPath)
        }
        if (localImageListPath.isNotEmpty()) {
            localImageListPath.forEach { cache ->
                if (!Account.userImageList.contains(cache))
                    filePathList.add(cache)
            }
        }
        if (filePathList.isNotEmpty()) {
            val apiResponse =
                mediaUploadRepository.uploadMultiplePicture(filePathList)
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                var avatarUrl = ""
                val imageList = arrayListOf<String>()
                apiResult.forEachIndexed { index, value ->
                    if (localAvatarPath.isNotBlank() && index == 0)
                        avatarUrl = value
                    else
                        imageList.add(value)
                }
                return Pair(avatarUrl, imageList)
            }
        }
        return Pair("", arrayListOf())
    }
}