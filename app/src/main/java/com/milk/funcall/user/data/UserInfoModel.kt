package com.milk.funcall.user.data

data class UserInfoModel(
    val avatarUrl: String = "",
    val fansNum: Int = 0,
    val followNum: Int = 0,
    val gender: String = "",
    val id: Long = 0,
    val uid: Long = 0,
    val link: String = "",
    val nickname: String = "",
    val notifyUnreadNum: String = "",
    val onlineState: String = "",
    val openid: String = "",
    val originalAvatarUrl: String = "",
    val selfIntroduction: String = "",
    val sortInfo: UserSortModel? = null,
    val userMaterialList: UserMediaModel? = null
)