package com.milk.funcall.chat.repo

import com.milk.funcall.account.Account
import com.milk.funcall.chat.ui.type.ChatMsgSendStatus
import com.milk.funcall.common.mdr.DataBaseManager
import com.milk.funcall.common.mdr.table.ChatMessageEntity

/** 私聊消息仓库、主要用于中转私聊消息 */
class ChatMessageRepository {

    /** 保存文本消息到数据库中 */
    internal fun saveTextChatMessage(messageContent: String, action: () -> ChatMessageEntity) {
        val textChatMessageEntity = action()
        textChatMessageEntity.messageContent = messageContent
        DataBaseManager.DB.chatMessageTableDao().insertMessage(textChatMessageEntity)
    }

    /** 网络请求结果私聊实体 */
    internal fun receiveChatMessageEntity(
        msgNetworkUniqueId: String,
        targetId: Long,
        messageType: Int,
        operationTime: Long
    ) = ChatMessageEntity().apply {
        this.msgLocalUniqueId =
            createMsgLocalUniqueId(Account.userId, targetId)
        this.msgNetworkUniqueId = msgNetworkUniqueId
        this.userId = Account.userId
        this.targetId = targetId
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
        messageType: Int,
        operationTime: Long
    ) = ChatMessageEntity().apply {
        this.msgLocalUniqueId = msgLocalUniqueId
        this.userId = Account.userId
        this.targetId = targetId
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
    internal fun updateSendMessageStatus(
        msgLocalUniqueId: String,
        sendSuccess: Boolean,
        msgNetworkUniqueId: String = ""
    ) {
        if (sendSuccess) {
            DataBaseManager.DB.chatMessageTableDao()
                .updateChatMsgSendStatus(msgLocalUniqueId, ChatMsgSendStatus.SendSuccess.value)
            DataBaseManager.DB.chatMessageTableDao()
                .updateChatMsgNetworkUniqueId(msgLocalUniqueId, msgNetworkUniqueId)
        } else DataBaseManager.DB.chatMessageTableDao()
            .updateChatMsgSendStatus(msgLocalUniqueId, ChatMsgSendStatus.SendFailed.value)
    }
}