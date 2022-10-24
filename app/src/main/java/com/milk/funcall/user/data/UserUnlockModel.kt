package com.milk.funcall.user.data

data class UserUnlockModel(
    val remainUnlockCount: Int = 0, // 剩余解锁次数
    val unlockMethod: Int = 0, // 解锁方式
    var unlockMedias: MutableList<Int> = mutableListOf()
)