package com.milk.funcall.common.mdr.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "ChatMessageTable",
    primaryKeys = ["chatUserId", "chatLocalMsgUniqueId"],
    indices = [Index(value = ["chatUserId", "chatLocalMsgUniqueId"], unique = true)]
)
class ChatMessageEntity {
    @ColumnInfo(name = "chatLocalMsgUniqueId")
    var msgLocalUniqueId: String = ""

    @ColumnInfo(name = "chatNetworkMsgUniqueId")
    var msgNetworkUniqueId: String = ""

    @ColumnInfo(name = "chatUserId")
    var userId: Long = 0

    @ColumnInfo(name = "chatTargetId")
    var targetId: Long = 0

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

    @ColumnInfo(name = "chatSendStatus")
    var sendStatus: Int = 0

    override fun toString(): String {
        return "messageUniqueId=$msgLocalUniqueId,msgNetworkUniqueId=$msgNetworkUniqueId," +
                "userId=$userId,targetId=$targetId,messageType=$messageType," +
                "operationTime=$operationTime,messageContent=$messageContent," +
                "isAcceptMessage=$isAcceptMessage,isReadMessage=$isReadMessage," +
                "isSendSuccess=$sendStatus"
    }
}