package com.milk.funcall.user.data

import com.google.gson.annotations.SerializedName
import com.milk.funcall.common.mdr.table.UserInfoEntity
import java.io.Serializable

data class UserTotalInfoModel(
    @SerializedName("fansNum")
    val userFans: Int = 0,
    @SerializedName("blackFlag")
    val isBlacked: Boolean = false,
    @SerializedName("followFlag")
    val isFollowed: Boolean = false,
    @SerializedName("followNum")
    val userFollows: Int = 0,
    // 展示给用户的 ID
    @SerializedName("uid")
    val userIdx: String = "",
    @SerializedName("link")
    val userLink: String = "",
    // 登录的 accessToken
    @SerializedName("openid")
    val openId: String = "",
    // 源头像地址未压缩
    @SerializedName("originalAvatarUrl")
    val UserOriginalAvatar: String = "",
    // 自我介绍说明
    @SerializedName("selfIntroduction")
    val userBio: String = "",
    @SerializedName("sortInfo")
    val userSortInfo: UserSortModel? = null,
    val userMaterialList: MutableList<UserMediaModel>? = null
) : UserInfoEntity(), Serializable