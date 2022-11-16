package com.milk.funcall.account.data

import com.google.gson.annotations.SerializedName

data class EditProfileBody(
    @SerializedName("avatarUrl") var avatarUrl: String = "",
    @SerializedName("nickname") var userName: String = "",
    @SerializedName("selfIntroduction") var userBio: String = "",
    @SerializedName("link") var userLink: String = "",
    @SerializedName("videoImageUrl") var videoImageUrl: String = "",
    @SerializedName("videoUrl") var videoUrl: String = "",
    @SerializedName("imgList") var imgList: ArrayList<String> = arrayListOf(),
)