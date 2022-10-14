package com.milk.funcall.account.api

import com.milk.funcall.account.data.BlackedListModel
import com.milk.funcall.common.response.ApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface BlackedApiService {
    @FormUrlEncoded
    @POST("/funcall/pageBlackedList")
    suspend fun getBlackedList(
        @Field("current") index: Int,
        @Field("size") size: Int = 20
    ): ApiResponse<BlackedListModel>
}