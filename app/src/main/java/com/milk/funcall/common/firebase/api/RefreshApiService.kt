package com.milk.funcall.common.firebase.api

import com.milk.funcall.common.response.ApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface RefreshApiService {
    @FormUrlEncoded
    @Headers("Encoded:false")
    @POST("/funcall/start/push")
    suspend fun uploadToken(
        @Field("refreshToken") token: String,
        @Field("deviceUniqueCode") deviceUniqueCode: String
    ): ApiResponse<Any>
}