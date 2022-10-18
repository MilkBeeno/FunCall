package com.milk.funcall.app

import com.milk.funcall.common.response.ApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AppService {
    @FormUrlEncoded
    @POST("/funcall/getMobileConf")
    suspend fun getConfig(
        @Field("appId") appId: String,
        @Field("versionCode") versionCode: String,
        @Field("channelCode") channelCode: String
    ): ApiResponse<MutableMap<String, String>>
}