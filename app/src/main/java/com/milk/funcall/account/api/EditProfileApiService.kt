package com.milk.funcall.account.api

import com.milk.funcall.account.data.EditProfileBody
import com.milk.funcall.common.response.ApiResponse
import com.milk.funcall.user.data.UserInfoModel
import retrofit2.http.Body
import retrofit2.http.POST

interface EditProfileApiService {
    @POST("/funcall/editProfile")
    suspend fun uploadProfile(
        @Body body: EditProfileBody
    ): ApiResponse<UserInfoModel>
}