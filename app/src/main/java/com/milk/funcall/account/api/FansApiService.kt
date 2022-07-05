package com.milk.funcall.account.api

import com.milk.funcall.account.data.FansModel
import com.milk.funcall.common.data.ApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface FansApiService {
    @FormUrlEncoded
    @POST("/funcall/pageFollowOrFans")
    suspend fun getFans(
        @Field("current") pageIndex: Int,
        @Field("ffType") type: String = "fans",
        @Field("size") size: Int = 8,
    ): ApiResponse<FansModel>
}