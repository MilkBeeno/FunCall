package com.milk.funcall.common.net

import com.milk.funcall.common.response.ApiResponse
import com.milk.funcall.common.net.error.ApiErrorCode
import com.milk.funcall.common.net.error.ApiErrorHandler

internal suspend fun <T> retrofit(
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
        ApiErrorHandler.post(ApiErrorCode.retrofitError, e.message.toString())
        ApiResponse(ApiErrorCode.retrofitError, e.message.toString())
    }
}