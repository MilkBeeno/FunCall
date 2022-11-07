package com.milk.funcall.user.api

import com.milk.funcall.common.response.ApiResponse
import com.milk.funcall.user.data.UserInfoModel
import com.milk.funcall.user.data.UserUnlockModel
import retrofit2.http.*

interface UserInfoApiService {
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

    @GET("/funcall/getUnlockInfo")
    suspend fun getUnlockInfo(
        @Query("deviceUniqueCode") deviceUniqueCode: String,
        @Query("freeLimitUnlock") freeUnlockTimes: Int,
        @Query("adLimitUnlock") viewAdUnlockTimes: Int,
        @Query("unlockUserId") unlockUserId: Long,
    ): ApiResponse<UserUnlockModel>

    @FormUrlEncoded
    @POST("/funcall/unlock/user")
    suspend fun changeUnlockStatus(
        @Field("deviceUniqueCode") deviceUniqueCode: String,
        // 解锁内容(1：联系方式，2：图片；3：视频)
        @Field("unlockContent") unlockType: Int,
        @Field("unlockUserId") unlockUserId: Long
    ): ApiResponse<Any>
}