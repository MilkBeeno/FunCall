package com.milk.funcall.login.net

import com.milk.funcall.user.data.UserInfoModel
import com.milk.funcall.common.data.ApiResponse
import com.milk.funcall.login.data.LoginModel
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface LoginApiService {

    @FormUrlEncoded
    @POST("/funcall/login")
    suspend fun login(
        @Field("deviceUniqueCode") deviceNum: String,
        @Field("oauthType") authType: String,
        @Field("openid") accessToken: String
    ): ApiResponse<LoginModel>

    @GET("/funcall/currentUserInfo")
    suspend fun obtainUserInfo():ApiResponse<UserInfoModel>
}