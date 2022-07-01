package com.milk.funcall.common.net

import com.milk.funcall.common.data.ApiResponse
import com.milk.funcall.common.net.handler.ApiCode
import com.milk.funcall.common.net.handler.ApiErrorHandler

suspend fun <T> retrofit(
    unifiedProcessing: Boolean = true,
    action: suspend () -> ApiResponse<T>
): ApiResponse<T> {
    return try {
        val response = action()
        when {
            response.code == 200 -> {
                response.success = true
            }
            unifiedProcessing -> {
                ApiErrorHandler.post(response.code, response.message)
            }
        }
        response
    } catch (e: Exception) {
        ApiErrorHandler.post(ApiCode.retrofitError, e.message.toString())
        ApiResponse(ApiCode.retrofitError, e.message.toString())
    }
}