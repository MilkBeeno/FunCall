package com.milk.funcall.account.api

import com.milk.funcall.common.data.ApiResponse
import com.milk.funcall.user.data.UserTotalInfoModel
import retrofit2.http.GET

interface AccountApiService {
    @GET("/funcall/currentUserInfo")
    suspend fun getUserInfo(): ApiResponse<UserTotalInfoModel>
}