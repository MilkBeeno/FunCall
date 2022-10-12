package com.milk.funcall.common.ad.data

data class AdResponseModel(
    val code: Int = 0,
    val message: String = "",
    val result: MutableList<AdModel>? = null
)