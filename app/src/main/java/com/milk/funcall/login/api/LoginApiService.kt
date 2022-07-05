package com.milk.funcall.login.api

import com.milk.funcall.common.data.ApiResponse
import com.milk.funcall.login.data.AvatarNameModel
import com.milk.funcall.login.data.LoginModel
import com.milk.funcall.user.data.UserTotalInfoModel
import retrofit2.http.*

interface LoginApiService {

    @FormUrlEncoded
    @POST("/funcall/login")
    suspend fun login(
        @Field("deviceUniqueCode") deviceNum: String,
        @Field("oauthType") authType: String,
        @Field("openid") accessToken: String
    ): ApiResponse<LoginModel>

    @GET("/funcall/currentUserInfo")
    suspend fun getUserInfo(): ApiResponse<UserTotalInfoModel>

    @GET("/funcall/getRandomBasicInfo")
    suspend fun getUserAvatarName(
        @Query("gender") gender: String
    ): ApiResponse<AvatarNameModel>
}