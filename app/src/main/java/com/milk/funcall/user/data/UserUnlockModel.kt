package com.milk.funcall.user.data

import com.google.gson.annotations.SerializedName

data class UserUnlockModel(
    val remainUnlockCount: Int = 0, // 剩余解锁次数
    val unlockMethod: Int = 0, // 解锁方式
    @SerializedName("unlockContentList")
    var unlockMedias: MutableList<Int> = mutableListOf()
)