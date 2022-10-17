package com.milk.funcall.account.ui.vm

import androidx.lifecycle.ViewModel
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.account.Account
import com.milk.funcall.account.repo.EditProfileRepository
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.media.uploader.MediaUploadRepository
import com.milk.simple.ktx.ioScope
import kotlinx.coroutines.flow.MutableSharedFlow

class EditProfileViewModel : ViewModel() {
    private val mediaUploadRepository by lazy { MediaUploadRepository() }
    private val editProfileRepository by lazy { EditProfileRepository() }

    /** 本地相册或拍照选择的头像 */
    var localAvatarPath: String = ""

    /** 本地相册或录像选择的个人视频 */
    var localVideoPath = ""

    /** 本地相册或拍照选择的个人图片 */
    val localImageListPath = mutableListOf<String>()

    /** 当前用户更新信息的状态、用户控制用户信息弹窗和请求结果提示 */
    var uploadResult = MutableSharedFlow<Boolean>()

    fun uploadProfile(name: String, bio: String, link: String) {
        ioScope {
            val avatarUrl = uploadAvatarProfile()
            val imageList = uploadImageListProfile()
            val videoUrl = uploadVideoProfile()
            val apiResponse = editProfileRepository
                .uploadProfile(avatarUrl, name, bio, link, videoUrl, imageList)
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                uploadResult.emit(true)
                Account.saveAccountInfo(apiResult)
                LiveEventBus.get<Boolean>(EventKey.REFRESH_HOME_LIST)
                    .post(true)
            } else {
                uploadResult.emit(false)
            }
        }
    }

    /** 更新头像信息 */
    private suspend fun uploadAvatarProfile(): String {
        val avatar = Account.userAvatar
        return if (localAvatarPath.isNotBlank() && localAvatarPath != avatar) {
            val apiResponse =
                mediaUploadRepository.uploadSinglePicture(localAvatarPath)
            val apiResult = apiResponse.data
            if (apiResponse.success && !apiResult.isNullOrEmpty()) {
                localAvatarPath = apiResult
                apiResult
            } else avatar
        } else avatar
    }

    private suspend fun uploadVideoProfile(): String {
        val avatar = Account.userVideo
        return if (localVideoPath.isNotBlank() && localVideoPath != avatar) {
            val apiResponse =
                mediaUploadRepository.uploadSingleVideo(localVideoPath)
            val apiResult = apiResponse.data
            if (apiResponse.success && !apiResult.isNullOrEmpty()) {
                localVideoPath = apiResult
                apiResult
            } else avatar
        } else avatar
    }

    /** 更新照片信息 */
    private suspend fun uploadImageListProfile(): ArrayList<String> {
        val resultImageList = arrayListOf<String>()
        val alreadyExistsImage = mutableListOf<String>()
        val uploadImageList = mutableListOf<String>()
        // 遍历当前选择图片并把要上传的图片添加到集合中
        localImageListPath.forEach { cache ->
            if (Account.userImageList.contains(cache)) {
                alreadyExistsImage.add(cache)
            } else {
                uploadImageList.add(cache)
            }
        }
        resultImageList.addAll(alreadyExistsImage)
        if (uploadImageList.isNotEmpty()) {
            val apiResponse =
                mediaUploadRepository.uploadMultiplePicture(uploadImageList)
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                apiResult.forEach { resultImageList.add(it) }
            } else {
                FireBaseManager.logEvent(FirebaseKey.UPLOAD_IMAGE_FAIL)
            }
        }
        return resultImageList
    }
}