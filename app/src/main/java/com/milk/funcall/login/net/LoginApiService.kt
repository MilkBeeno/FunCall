package com.milk.funcall.login.net

import com.milk.funcall.common.data.ApiResponse
import com.milk.funcall.login.data.AvatarNameModel
import com.milk.funcall.login.data.LoginModel
import com.milk.funcall.user.data.UserInfoModel
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
    suspend fun obtainUserInfo(): ApiResponse<UserInfoModel>

    @GET("/funcall/currentUserInfo")
    suspend fun obtainUserDefault(
        @Query("gender") gender: String
    ): ApiResponse<AvatarNameModel>
}