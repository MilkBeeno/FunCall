package com.milk.funcall.common.mdr.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "ChatMessageTable",
    primaryKeys = ["chatUserId", "chatMsgUniqueId"],
    indices = [Index(value = ["chatUserId", "chatMsgUniqueId"], unique = true)]
)
class ChatMessageEntity {
    @ColumnInfo(name = "chatMsgUniqueId")
    var messageUniqueId: String = ""

    @ColumnInfo(name = "chatUserId")
    var userId: Long = 0

    @ColumnInfo(name = "chatTargetId")
    var targetId: Long = 0

    @ColumnInfo(name = "chatTargetName")
    var targetName: String = ""

    @ColumnInfo(name = "chatTargetAvatar")
    var targetAvatar: String = ""

    @ColumnInfo(name = "chatMsgType")
    var messageType: Int = 0

    @ColumnInfo(name = "chatOperationTime")
    var operationTime: Long = 0

    @ColumnInfo(name = "chatMsgContent")
    var messageContent: String = ""

    @ColumnInfo(name = "chatIsAcceptMsg")
    var isAcceptMessage: Boolean = false

    @ColumnInfo(name = "chatIsReadMsg")
    var isReadMessage: Boolean = false

    @ColumnInfo(name = "chatIsSendSuccess")
    var isSendSuccess: Boolean = false

    override fun toString(): String {
        return "messageUniqueId=$messageUniqueId,userId=$userId,targetId=$targetId," +
                "targetName=$targetName,targetAvatar=$targetAvatar,messageType=$messageType," +
                "operationTime=$operationTime,messageContent=$messageContent," +
                "isAcceptMessage=$isAcceptMessage,isReadMessage=$isReadMessage," +
                "isSendSuccess=$isSendSuccess"
    }
}