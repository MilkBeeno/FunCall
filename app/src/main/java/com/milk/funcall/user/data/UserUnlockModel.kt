package com.milk.funcall.user.data

data class UserUnlockModel(
    val isUnlock: String = "", // 是否解锁(0：未解锁；1：已解锁)
    val remainUnlockCount: Int = 0, // 剩余解锁次数
    val unlockMethod: Int = 0 // 解锁方案
)