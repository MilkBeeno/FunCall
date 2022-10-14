package com.milk.funcall.common.db.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "ConversationTable",
    primaryKeys = ["conversationAccountId", "conversationTargetId"],
    indices = [Index(value = ["conversationAccountId", "conversationTargetId"], unique = true)]
)
class ConversationEntity {

    @ColumnInfo(name = "conversationAccountId")
    var accountId: Long = 0

    @ColumnInfo(name = "conversationTargetId")
    var targetId: Long = 0

    /** 接受私聊消息时、从服务端拉取的昵称、不更改不同步昵称 */
    @ColumnInfo(name = "conversationTargetName")
    var targetName: String = ""

    /** 接受私聊消息时、从服务端拉取的昵称、不更改不同步头像 */
    @ColumnInfo(name = "conversationTargetAvatar")
    var targetAvatar: String = ""

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

    @ColumnInfo(name = "conversationPutTopTime")
    var putTopTime: Long = 0

    override fun toString(): String {
        return "userId=$accountId,targetId=$targetId,messageType=$messageType," +
                "operationTime=$operationTime,messageContent=$messageContent," +
                "isAcceptMessage=$isAcceptMessage,unReadCount=$unReadCount," +
                "sendStatus=$sendStatus,putTopTime=$putTopTime"
    }
}