package com.milk.funcall.common.data

data class ApiResponse<T>(
    val code: Int = 0,
    val message: String = "",
    val data: T? = null,
    var success: Boolean = false,
    var timestamp: Long = 0L
)