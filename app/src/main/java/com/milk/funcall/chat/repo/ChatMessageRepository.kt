package com.milk.funcall.chat.repo

import com.milk.funcall.account.Account
import com.milk.funcall.chat.status.ChatMsgSendStatus
import com.milk.funcall.common.db.DataBaseManager
import com.milk.funcall.common.db.table.ChatMessageEntity

/** 私聊消息仓库、主要用于中转私聊消息 */
class ChatMessageRepository {

    /** 保存文本消息到数据库中 */
    internal fun saveTextMessage(messageContent: String, action: () -> ChatMessageEntity) {
        val textChatMessageEntity = action()
        textChatMessageEntity.messageContent = messageContent
        DataBaseManager.DB.chatMessageTableDao().insert(textChatMessageEntity)
    }

    /** 网络请求结果私聊实体 */
    internal fun receiveChatMessageEntity(
        msgNetworkUniqueId: String,
        targetId: Long,
        targetName: String,
        targetAvatar: String,
        messageType: Int,
        operationTime: Long
    ) = ChatMessageEntity().apply {
        this.msgLocalUniqueId =
            createMsgLocalUniqueId(Account.userId, targetId)
        this.msgNetworkUniqueId = msgNetworkUniqueId
        this.accountId = Account.userId
        this.targetId = targetId
        this.targetName = targetName
        this.targetAvatar = targetAvatar
        this.messageType = messageType
        this.operationTime = operationTime
        this.isReadMessage = false
        this.isAcceptMessage = true
        this.sendStatus = ChatMsgSendStatus.SendSuccess.value
    }

    /** 网络请求发送私聊实体 */
    internal fun sendChatMessageEntity(
        msgLocalUniqueId: String,
        targetId: Long,
        targetName: String,
        targetAvatar: String,
        messageType: Int,
        operationTime: Long
    ) = ChatMessageEntity().apply {
        this.msgLocalUniqueId = msgLocalUniqueId
        this.accountId = Account.userId
        this.targetId = targetId
        this.targetName = targetName
        this.targetAvatar = targetAvatar
        this.messageType = messageType
        this.operationTime = operationTime
        this.isReadMessage = true
        this.isAcceptMessage = false
        this.sendStatus = ChatMsgSendStatus.Sending.value
    }

    /** 获取消息唯一 ID */
    internal fun createMsgLocalUniqueId(userId: Long, targetId: Long) =
        System.currentTimeMillis().toString()
            .plus("-")
            .plus(userId)
            .plus("-")
            .plus(targetId)

    /** 本地数据发送状态更新 */
    internal fun updateSendStatus(
        msgLocalUniqueId: String,
        sendSuccess: Boolean,
        msgNetworkUniqueId: String = ""
    ) {
        if (sendSuccess) {
            DataBaseManager.DB.chatMessageTableDao()
                .updateSendStatus(msgLocalUniqueId, ChatMsgSendStatus.SendSuccess.value)
            DataBaseManager.DB.chatMessageTableDao()
                .updateNetworkUniqueId(msgLocalUniqueId, msgNetworkUniqueId)
        } else DataBaseManager.DB.chatMessageTableDao()
            .updateSendStatus(msgLocalUniqueId, ChatMsgSendStatus.SendFailed.value)
    }
}