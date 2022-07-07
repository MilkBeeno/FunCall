package com.milk.funcall.user.api

import com.milk.funcall.common.data.ApiResponse
import com.milk.funcall.user.data.UserTotalInfoModel
import retrofit2.http.GET
import retrofit2.http.Query

interface UserTotalInfoApiService {
    @GET("/funcall/getUser")
    suspend fun getUserTotalInfo(
        @Query("userId") userId: Long
    ): ApiResponse<UserTotalInfoModel>

    @GET("/funcall/randomGetUser")
    suspend fun getNextUserTotalInfo(
        @Query("gender") gender: String
    ): ApiResponse<UserTotalInfoModel>
}