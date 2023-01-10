package com.milk.funcall.square.api

import com.milk.funcall.common.response.ApiResponse
import com.milk.funcall.square.data.SquareModel
import retrofit2.http.GET
import retrofit2.http.Query

interface SquareApiService {

    @GET("/funcall/news/match/startInfo")
    suspend fun getSquareInfo(@Query("gender") gender: String): ApiResponse<SquareModel>
}