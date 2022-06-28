package com.milk.funcall.common.net

import com.milk.funcall.common.data.ApiResponse
import com.milk.simple.log.Logger


suspend fun <T> retrofitCatch(action: suspend () -> ApiResponse<T>): ApiResponse<T> {
    return try {
        action().apply { success = code == 200 }
    } catch (e: Exception) {
        Logger.d("Request is:${e.message}", "ApiError")
        ApiResponse(code = 100, message = e.message.toString())
    }
}