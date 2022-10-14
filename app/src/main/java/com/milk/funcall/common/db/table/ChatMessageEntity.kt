package com.milk.funcall.common.db.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "ChatMessageTable",
    primaryKeys = ["chatAccountId", "chatLocalMsgUniqueId"],
    indices = [Index(value = ["chatAccountId", "chatLocalMsgUniqueId"], unique = true)]
)
class ChatMessageEntity {
    /** 用户数据库更新本地数据状态的唯一消息 ID */
    @ColumnInfo(name = "chatLocalMsgUniqueId")
    var msgLocalUniqueId: String = ""

    /** 用户数据库操更新网络异地消息的唯一消息 ID */
    @ColumnInfo(name = "chatNetworkMsgUniqueId")
    var msgNetworkUniqueId: String = ""

    @ColumnInfo(name = "chatAccountId")
    var accountId: Long = 0

    @ColumnInfo(name = "chatTargetId")
    var targetId: Long = 0

    /** 接受私聊消息时、从服务端拉取的昵称、不更改不同步昵称 */
    @ColumnInfo(name = "chatTargetName")
    var targetName: String = ""

    /** 接受私聊消息时、从服务端拉取的昵称、不更改不同步头像 */
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

    @ColumnInfo(name = "chatSendStatus")
    var sendStatus: Int = 0

    override fun toString(): String {
        return "messageUniqueId=$msgLocalUniqueId,msgNetworkUniqueId=$msgNetworkUniqueId," +
                "accountId=$accountId,targetId=$targetId,messageType=$messageType," +
                "operationTime=$operationTime,messageContent=$messageContent," +
                "isAcceptMessage=$isAcceptMessage,isReadMessage=$isReadMessage," +
                "isSendSuccess=$sendStatus"
    }
}