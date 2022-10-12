package com.milk.funcall.common.ad.data

data class AdPositionModel(
    val adType: Int,
    val adv: String,
    val limitClickCount: Int,
    val limitShowCount: Int,
    var posId: String,
    val ratio: Int,
    val typeRatio: Int,
    var unitId: String
)