package com.milk.funcall.square.data

data class SquareModel(
    var unlockCount: Int = 0,
    var unlockFreeCount: Int = 0,
    var userAvatarList: MutableList<String>? = null
)