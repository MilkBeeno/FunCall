package com.milk.funcall.user.data

import com.google.gson.annotations.SerializedName
import com.milk.funcall.common.mdr.table.UserInfoEntity
import java.io.Serializable

data class UserInfoModel(
    @SerializedName("fansNum")
    val targetFans: Int = 0,
    @SerializedName("blackFlag")
    val isBlacked: Boolean = false,
    @SerializedName("followFlag")
    var isFollowed: Boolean = false,
    @SerializedName("followNum")
    val userFollows: Int = 0,
    // 展示给用户的 ID
    @SerializedName("uid")
    val targetIdx: String = "",
    @SerializedName("link")
    val targetLink: String = "",
    // 登录的 accessToken
    @SerializedName("openid")
    val openId: String = "",
    // 源头像地址未压缩
    @SerializedName("originalAvatarUrl")
    val targetOriginalAvatar: String = "",
    // 自我介绍说明
    @SerializedName("selfIntroduction")
    val targetBio: String = "",
    @SerializedName("sortInfo")
    val targetSortInfo: UserSortModel? = null,
    // 用户的视频地址
    @SerializedName("videoMaterial")
    val targetVideoUrl: UserMediaModel? = null,
    // 用户照片地址
    @SerializedName("imageMaterialList")
    var targetImageList: MutableList<UserMediaModel>? = null
) : UserInfoEntity(), Serializable {

    fun imageListConvert(): MutableList<String> {
        val imageUrlList = mutableListOf<String>()
        targetImageList?.forEach { imageUrlList.add(it.thumbUrl) }
        return imageUrlList
    }

    fun videoConvert(): String {
        return if (targetVideoUrl == null || targetVideoUrl.toString() == "null") ""
        else targetVideoUrl.toString()
    }
}