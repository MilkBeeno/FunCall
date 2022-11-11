package com.milk.funcall.user.api

import com.milk.funcall.common.response.ApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ReportApiService {
    @FormUrlEncoded
    @POST("/funcall/tipOff")
    suspend fun report(
        @Field("userId") userId: Long,
        @Field("reportType") reportType: Int,
    ): ApiResponse<Any?>
}