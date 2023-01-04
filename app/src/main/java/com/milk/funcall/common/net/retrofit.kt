package com.milk.funcall.common.net

import com.milk.funcall.common.net.error.ApiErrorCode
import com.milk.funcall.common.response.ApiResponse

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
                // 进行错误统一处理
            }
        }
        response
    } catch (e: Exception) {
        // 进行错误统一处理
        ApiResponse(ApiErrorCode.retrofitError, e.message.toString())
    }
}