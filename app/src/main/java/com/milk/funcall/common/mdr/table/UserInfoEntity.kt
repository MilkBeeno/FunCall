package com.milk.funcall.common.mdr.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "UserInfoTable",
    primaryKeys = ["UserId"],
    indices = [Index(value = ["UserId"], unique = true)]
)
open class UserInfoEntity {
    @SerializedName("id")
    @ColumnInfo(name = "UserId")
    var userId: Long = 0

    @SerializedName("nickname")
    @ColumnInfo(name = "UserName")
    var userName: String = ""

    @SerializedName("avatarUrl")
    @ColumnInfo(name = "UserAvatarUrl")
    var userAvatar: String = ""

    @SerializedName("gender")
    @ColumnInfo(name = "UserGender")
    var userGender: String = ""

    @SerializedName("imageUrl")
    @ColumnInfo(name = "UserImageUrl")
    var userImage: String = ""

    @SerializedName("videoUrl")
    @ColumnInfo(name = "UserVideoUrl")
    var userVideo: String = ""

    @SerializedName("onlineState")
    @ColumnInfo(name = "UserOnlineState")
    var userOnline: String = ""

    override fun toString(): String {
        return "userId=$userId,userAvatar=$userAvatar,userGender=$userGender,userImage=$userImage," +
                "userName=$userName,userVideo=$userVideo,userOnline=$userOnline"
    }
}