package com.milk.funcall.user.data

import com.milk.funcall.common.mdr.table.UserInfoEntity
import java.io.Serializable

data class UserTotalInfoModel(
    val fansNum: Int = 0,
    val followNum: Int = 0,
    val uid: Long = 0,
    val link: String = "",
    val notifyUnreadNum: String = "",
    val openid: String = "",
    val originalAvatarUrl: String = "",
    val selfIntroduction: String = "",
    val sortInfo: UserSortModel? = null,
    val userMaterialList: MutableList<UserMediaModel>? = null
) : UserInfoEntity(), Serializable