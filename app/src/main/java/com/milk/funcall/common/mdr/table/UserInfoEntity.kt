package com.milk.funcall.common.mdr.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "UserInfoTable",
    primaryKeys = ["userInfoUserId", "userInfoTargetId"],
    indices = [Index(value = ["userInfoUserId", "userInfoTargetId"], unique = true)]
)
open class UserInfoEntity {
    @ColumnInfo(name = "userInfoUserId")
    var userId: Long = 0

    @SerializedName("id")
    @ColumnInfo(name = "userInfoTargetId")
    var targetId: Long = 0

    @SerializedName("nickname")
    @ColumnInfo(name = "userInfoName")
    var userName: String = ""

    @SerializedName("avatarUrl")
    @ColumnInfo(name = "userInfoAvatarUrl")
    var userAvatar: String = ""

    @SerializedName("gender")
    @ColumnInfo(name = "userInfoGender")
    var userGender: String = ""

    @SerializedName("imageUrl")
    @ColumnInfo(name = "userInfoImageUrl")
    var userImage: String = ""

    @SerializedName("videoUrl")
    @ColumnInfo(name = "userInfoVideoUrl")
    var userVideo: String = ""

    @SerializedName("onlineState")
    @ColumnInfo(name = "userInfoOnlineState")
    var userOnline: String = ""

    override fun toString(): String {
        return "userId=$userId,targetId=$targetId,userAvatar=$userAvatar,userGender=$userGender," +
                "userImage=$userImage,userName=$userName,userVideo=$userVideo,userOnline=$userOnline"
    }
}