package com.milk.funcall.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore

@Entity(tableName = "MessageTable", primaryKeys = ["messageUniqueId"])
data class MessageEntity(
    @ColumnInfo(name = "messageUniqueId")
    var messageUniqueId: String = "",
    @ColumnInfo(name = "userId")
    var userId: Long = 0,
    @ColumnInfo(name = "targetId")
    var targetId: Long = 0,
    @ColumnInfo(name = "targetName")
    var targetName: String = "",
    @ColumnInfo(name = "targetImage")
    var targetImage: String = "",
    @ColumnInfo(name = "operationTime")
    var operationTime: Long = 0,
    @ColumnInfo(name = "messageContent")
    var messageContent: String = ""
) {
    companion object {
        @Ignore
        fun createUid(userId: Long, targetId: Long) =
            System.currentTimeMillis().toString()
                .plus("-")
                .plus(userId)
                .plus("-")
                .plus(targetId)
    }
}