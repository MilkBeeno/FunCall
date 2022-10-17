package com.milk.funcall.account.data

import com.google.gson.annotations.SerializedName

data class BlackedModel(
    @SerializedName("id") var userId: Long = 0,
    @SerializedName("nickname") var userName: String = "",
    @SerializedName("avatarUrl") val userAvatar: String = "",
    @SerializedName("gender") var userGender: String = ""
)