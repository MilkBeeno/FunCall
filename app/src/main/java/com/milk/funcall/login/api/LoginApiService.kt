package com.milk.funcall.login.api

import com.milk.funcall.common.response.ApiResponse
import com.milk.funcall.login.data.AvatarNameModel
import com.milk.funcall.login.data.LoginModel
import retrofit2.http.*

interface LoginApiService {

    @FormUrlEncoded
    @POST("/funcall/login")
    suspend fun login(
        @Field("deviceUniqueCode") deviceNum: String,
        @Field("oauthType") authType: String,
        @Field("openid") accessToken: String
    ): ApiResponse<LoginModel>

    @GET("/funcall/getRandomBasicInfo")
    suspend fun getUserAvatarName(
        @Query("gender") gender: String
    ): ApiResponse<AvatarNameModel>

    @FormUrlEncoded
    @Headers("Encoded:false")
    @POST("/funcall/registeredUser")
    suspend fun updateUserProfile(
        @Field("nickname") nickName: String,
        @Field("avatarUrl") avatarUrl: String,
        @Field("gender") gender: String
    ): ApiResponse<String>
}