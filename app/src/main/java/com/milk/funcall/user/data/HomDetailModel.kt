package com.milk.funcall.user.data

import com.google.gson.annotations.SerializedName

data class HomDetailModel(
    @SerializedName("avatarUrl")
    val userAvatar: String,
    @SerializedName("gender")
    val userGender: String = "",
    @SerializedName("id")
    val userId: Long = 0,
    @SerializedName("imageUrl")
    val userImage: String = "",
    @SerializedName("nickname")
    val userName: String = "",
    val onlineState: String = "",
    @SerializedName("videoUrl")
    val userVideo: String = ""
)