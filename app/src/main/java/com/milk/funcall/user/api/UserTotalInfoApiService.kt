package com.milk.funcall.user.api

import com.milk.funcall.common.data.ApiResponse
import com.milk.funcall.user.data.UserTotalInfoModel
import retrofit2.http.*

interface UserTotalInfoApiService {
    @GET("/funcall/getUser")
    suspend fun getUserTotalInfo(
        @Query("userId") userId: Long
    ): ApiResponse<UserTotalInfoModel>

    @GET("/funcall/randomGetUser")
    suspend fun getNextUserTotalInfo(
        @Query("gender") gender: String
    ): ApiResponse<UserTotalInfoModel>

    @FormUrlEncoded
    @POST("/funcall/follow")
    suspend fun changeFollowState(
        @Field("faceUserId") targetId: Long,
        @Field("followFlag") isFollow: Boolean
    ): ApiResponse<Any>
}