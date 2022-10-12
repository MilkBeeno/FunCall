package com.milk.funcall.common.ad.data

data class AdModel(
    val code: String = "",
    val positionList: MutableList<AdPositionModel>? = null
)