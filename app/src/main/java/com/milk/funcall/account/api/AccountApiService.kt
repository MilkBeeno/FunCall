package com.milk.funcall.account.api

import com.milk.funcall.common.response.ApiResponse
import com.milk.funcall.user.data.UserInfoModel
import retrofit2.http.GET

interface AccountApiService {
    @GET("/funcall/currentUserInfo")
    suspend fun getUserInfo(): ApiResponse<UserInfoModel>
}