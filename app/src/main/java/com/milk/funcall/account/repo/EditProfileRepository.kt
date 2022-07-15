package com.milk.funcall.account.repo

import com.milk.funcall.account.api.ApiService
import com.milk.funcall.account.data.EditProfileBody
import com.milk.funcall.common.net.retrofit

class EditProfileRepository {
    suspend fun uploadProfile(
        avatarUrl: String,
        userName: String,
        userBio: String,
        userLink: String,
        videoUrl: String,
        imgList: ArrayList<String>,
    ) = retrofit {
        val body = EditProfileBody(avatarUrl, userName, userBio, userLink, videoUrl, imgList)
        ApiService.editProfileApiService.uploadProfile(body)
    }
}