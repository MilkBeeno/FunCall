package com.milk.funcall.common.ad.api

import com.milk.funcall.common.ad.data.AdModel
import com.milk.funcall.common.data.ApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AdApiService {
    @FormUrlEncoded
    @POST("/funcall/getMobileConf")
    suspend fun getAdSwitch(
        @Field("appId") appId: String,
        @Field("versionCode") versionCode: String,
        @Field("channelCode") channelCode: String
    ): ApiResponse<MutableMap<String, String>>

    @FormUrlEncoded
    @POST("/funcall/listAdPositionInfo")
    suspend fun getAdConfig(
        @Field("appId") appId: String,
        @Field("versionCode") versionCode: String,
        @Field("channelCode") channelCode: String
    ): ApiResponse<MutableList<AdModel>>
}