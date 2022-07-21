package com.milk.funcall.common.mdr.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "ConversationTable",
    primaryKeys = ["conversationUserId", "conversationTargetId"],
    indices = [Index(value = ["conversationUserId", "conversationTargetId"], unique = true)]
)
class ConversationEntity {

    @ColumnInfo(name = "conversationUserId")
    var userId: Long = 0

    @ColumnInfo(name = "conversationTargetId")
    var targetId: Long = 0

    @ColumnInfo(name = "conversationMsgType")
    var messageType: Int = 0

    @ColumnInfo(name = "conversationOperationTime")
    var operationTime: Long = 0

    @ColumnInfo(name = "conversationMsgContent")
    var messageContent: String = ""

    @ColumnInfo(name = "conversationIsAcceptMsg")
    var isAcceptMessage: Boolean = false

    @ColumnInfo(name = "conversationUnReadCount")
    var unReadCount: Int = 0

    @ColumnInfo(name = "conversationSendStatus")
    var sendStatus: Int = 0

    override fun toString(): String {
        return "userId=$userId,targetId=$targetId,messageType=$messageType," +
                "operationTime=$operationTime,messageContent=$messageContent," +
                "isAcceptMessage=$isAcceptMessage,unReadCount=$unReadCount," +
                "sendStatus=$sendStatus"
    }
}