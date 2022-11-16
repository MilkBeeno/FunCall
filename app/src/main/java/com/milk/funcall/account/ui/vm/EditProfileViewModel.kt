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

    /** 编辑页面--本地相册或拍照选择的头像地址 */
    internal var editAvatarPath: String = ""

    /** 编辑页面--本地相册或录像选择的视频封面地址 */
    internal var editVideoImagePath = ""

    /** 编辑页面--本地相册或录像选择的视频地址 */
    internal var editVideoPath = ""

    /** 编辑页面--本地相册或拍照选择的个人图片地址 */
    internal val editImageListPath = mutableListOf<String>()

    /** 当前用户更新信息的状态、用户控制用户信息弹窗和请求结果提示 */
    internal var uploadResultFlow = MutableSharedFlow<Boolean>()

    internal fun uploadProfile(name: String, bio: String, link: String) {
        ioScope {
            val avatarUrl = uploadAvatarProfile()
            val videoImageUrl = uploadAvatarProfile()
            val videoUrl = uploadVideoProfile()
            val imageList = uploadImageListProfile()
            val apiResponse = editProfileRepository
                .uploadProfile(avatarUrl, name, bio, link, videoImageUrl, videoUrl, imageList)
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                uploadResultFlow.emit(true)
                Account.saveAccountInfo(apiResult)
                LiveEventBus.get<Boolean>(EventKey.REFRESH_HOME_LIST)
                    .post(true)
            } else {
                uploadResultFlow.emit(false)
            }
        }
    }

    /** 更新头像信息 */
    private suspend fun uploadAvatarProfile(): String {
        val avatar = Account.userAvatar
        return if (editAvatarPath.isNotBlank() && editAvatarPath != avatar) {
            val apiResponse =
                mediaUploadRepository.uploadSinglePicture(editAvatarPath)
            val apiResult = apiResponse.data
            if (apiResponse.success && !apiResult.isNullOrEmpty()) {
                editAvatarPath = apiResult
                apiResult
            } else avatar
        } else avatar
    }

    private suspend fun uploadVideoProfile(): String {
        val avatar = Account.userVideo
        return if (editVideoPath.isNotBlank() && editVideoPath != avatar) {
            val apiResponse =
                mediaUploadRepository.uploadSingleVideo(editVideoPath)
            val apiResult = apiResponse.data
            if (apiResponse.success && !apiResult.isNullOrEmpty()) {
                editVideoPath = apiResult
                apiResult
            } else avatar
        } else avatar
    }

    /** 更新照片信息 */
    private suspend fun uploadImageListProfile(): ArrayList<String> {
        val resultImageList = arrayListOf<String>()
        val existsImageList = mutableListOf<String>()
        val uploadImageList = mutableListOf<String>()
        // 遍历当前选择图片并把要上传的图片添加到集合中
        editImageListPath.forEach { cache ->
            if (Account.userImageList.contains(cache)) {
                existsImageList.add(cache)
            } else {
                uploadImageList.add(cache)
            }
        }
        resultImageList.addAll(existsImageList)
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