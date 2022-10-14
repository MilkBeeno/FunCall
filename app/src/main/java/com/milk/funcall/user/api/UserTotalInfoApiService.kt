package com.milk.funcall.user.api

import com.milk.funcall.common.response.ApiResponse
import com.milk.funcall.user.data.UserInfoModel
import retrofit2.http.*

interface UserTotalInfoApiService {
    @GET("/funcall/getUser")
    suspend fun getUserInfoByNetwork(
        @Query("userId") userId: Long
    ): ApiResponse<UserInfoModel>

    @GET("/funcall/randomGetUser")
    suspend fun getNextUserInfoByNetwork(
        @Query("gender") gender: String
    ): ApiResponse<UserInfoModel>

    @FormUrlEncoded
    @POST("/funcall/follow")
    suspend fun changeFollowedStatus(
        @Field("faceUserId") targetId: Long,
        @Field("followFlag") isFollow: Boolean
    ): ApiResponse<Any>

    @GET("/funcall/blackUser")
    suspend fun blackUser(
        @Query("blackedUserId") targetId: Long
    ): ApiResponse<Any>
}