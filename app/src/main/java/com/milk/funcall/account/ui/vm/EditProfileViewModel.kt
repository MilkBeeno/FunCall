package com.milk.funcall.account.ui.vm

import androidx.lifecycle.ViewModel
import com.milk.funcall.account.Account
import com.milk.funcall.account.repo.EditProfileRepository
import com.milk.funcall.common.media.uploader.MediaUploadRepository
import com.milk.simple.ktx.ioScope
import kotlinx.coroutines.flow.MutableSharedFlow

class EditProfileViewModel : ViewModel() {
    private val mediaUploadRepository by lazy { MediaUploadRepository() }
    private val editProfileRepository by lazy { EditProfileRepository() }

    /** 本地相册或拍照选择的头像 */
    var localAvatarPath: String = ""

    /** 本地相册或拍照选择的个人图片 */
    val localImageListPath = mutableListOf<String>()

    /** 本地相册或录像选择的个人视频 */
    var localVideoPath = ""

    /** 当前用户更新信息的状态 */
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
                apiResult.mediaConvert()
                Account.saveAccountInfo(apiResult)
            } else uploadResult.emit(false)
        }
    }

    private suspend fun uploadMediaProfile(): Pair<String, ArrayList<String>> {
        var avatarUrl = Account.userAvatar
        val imageList = arrayListOf<String>()
        // 已经上传过且没有删除图片集合
        val alreadyExistsImage = mutableListOf<String>()
        // 将要上传到服务器中图片的集合
        val uploadImageList = mutableListOf<String>()
        // 用户头像选择不为空、添加到上传集合中
        if (localAvatarPath.isNotBlank())
            uploadImageList.add(localAvatarPath)
        // 遍历当前选择图片并把要上传的图片添加到集合中
        localImageListPath.forEach { cache ->
            if (Account.userImageList.contains(cache))
                alreadyExistsImage.add(cache)
            else
                uploadImageList.add(cache)
        }
        if (uploadImageList.isNotEmpty()) {
            val apiResponse =
                mediaUploadRepository.uploadMultiplePicture(uploadImageList)
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                apiResult.forEachIndexed { index, value ->
                    // 已经上传头像、将返回的头像地址第一个元素赋值给头像
                    if (localAvatarPath.isNotBlank() && index == 0)
                        avatarUrl = value
                    else
                        imageList.add(value)
                }
                imageList.addAll(alreadyExistsImage)
            }
        }
        return Pair(avatarUrl, imageList)
    }
}