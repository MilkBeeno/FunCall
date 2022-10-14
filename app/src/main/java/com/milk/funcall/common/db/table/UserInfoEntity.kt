package com.milk.funcall.common.db.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "UserInfoTable",
    primaryKeys = ["userInfoAccountId", "userInfoTargetId"],
    indices = [Index(value = ["userInfoAccountId", "userInfoTargetId"], unique = true)]
)
open class UserInfoEntity {
    /** 1.登录时此 ID 为空、 2. 查看其他用户信息的保存到用户信息表当前用户 ID */
    @ColumnInfo(name = "userInfoAccountId")
    var accountId: Long = 0

    /** 1.登录时 TargetId 是当前登录用户的 ID 2. 查看用户信息时是他人的用户 ID */
    @SerializedName("id")
    @ColumnInfo(name = "userInfoTargetId")
    var targetId: Long = 0

    @SerializedName("nickname")
    @ColumnInfo(name = "userInfoName")
    var targetName: String = ""

    @SerializedName("avatarUrl")
    @ColumnInfo(name = "userInfoAvatarUrl")
    var targetAvatar: String = ""

    @SerializedName("gender")
    @ColumnInfo(name = "userInfoGender")
    var targetGender: String = ""

    @SerializedName("imageUrl")
    @ColumnInfo(name = "userInfoImageUrl")
    var targetImage: String = ""

    @SerializedName("videoUrl")
    @ColumnInfo(name = "userInfoVideoUrl")
    var targetVideo: String = ""

    @SerializedName("onlineState")
    @ColumnInfo(name = "userInfoOnlineState")
    var targetOnline: String = ""

    @SerializedName("blackFlag")
    @ColumnInfo(name = "userInfoIsBlacked")
    var targetIsBlacked: Boolean = false

    @SerializedName("followFlag")
    @ColumnInfo(name = "userInfoIsFollowed")
    var targetIsFollowed: Boolean = false

    override fun toString(): String {
        return "targetId=$targetId,targetAvatar=$targetAvatar,targetGender=$targetGender," +
                "targetImage=$targetImage,targetName=$targetName,targetVideo=$targetVideo," +
                "targetOnline=$targetOnline,targetIsBlacked=$targetIsBlacked," +
                "targetIsFollowed=$targetIsFollowed"
    }
}