package com.milk.funcall.common.response

/**
 * Paging3 分页展示模型
 */
data class ApiPagingResponse<T>(
    val code: Int = 0,
    val message: String = "",
    val data: MutableList<T>? = null
)