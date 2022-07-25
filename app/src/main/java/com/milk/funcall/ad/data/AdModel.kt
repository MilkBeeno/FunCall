package com.milk.funcall.ad.data

data class AdModel(
    val code: String = "",
    val positionList: MutableList<AdPositionModel>? = null
)