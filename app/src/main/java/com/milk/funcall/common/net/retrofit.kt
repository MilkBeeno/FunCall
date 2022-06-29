package com.milk.funcall.common.net

import com.milk.funcall.common.data.ApiResponse
import com.milk.funcall.common.net.handler.ApiErrorHandler


suspend fun <T> retrofitCatch(
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
        ApiErrorHandler.post(ApiErrorHandler.retrofitCatchCode, e.message.toString())
        ApiResponse(ApiErrorHandler.retrofitCatchCode, e.message.toString())
    }
}