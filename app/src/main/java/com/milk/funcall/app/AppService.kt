package com.milk.funcall.app

import com.milk.funcall.common.pay.GoogleSubsModel
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

    @FormUrlEncoded
    @POST("/google/pay/checkOrder")
    suspend fun getSubscribeStatus(
        @Field("packageName") packageName: String,
        @Field("productId") productId: String,
        @Field("purchaseToken") purchaseToken: String,
    ): ApiResponse<GoogleSubsModel>
}