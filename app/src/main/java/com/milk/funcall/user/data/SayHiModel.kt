package com.milk.funcall.user.data

import com.google.gson.annotations.SerializedName

data class SayHiModel(
    @SerializedName("avatarUrl")
    val userAvatar: String = "",
    @SerializedName("followFlag")
    val userFollowFlag: Boolean = false,
    @SerializedName("gender")
    val userGender: String = "",
    @SerializedName("id")
    val userId: Long = 0,
    @SerializedName("nickname")
    val userName: String = ""
)