package com.milk.funcall.chat.repo

import com.milk.funcall.account.Account
import com.milk.funcall.chat.ui.type.ChatMsgSendStatus
import com.milk.funcall.common.mdr.DataBaseManager
import com.milk.funcall.common.mdr.table.ConversationEntity

/** MessageFragment 页面会话列表数据 */
class ConversationRepository {

    /** 会话列表数据表 */
    internal fun saveConversation(
        targetId: Long,
        messageType: Int,
        operationTime: Long,
        isAcceptMessage: Boolean,
        sendStatus: Int,
        messageContent: String = ""
    ) {
        val unReadCount = if (isAcceptMessage) {
            val oldChatConversation =
                DataBaseManager.DB.conversationTableDao().query(Account.userId, targetId)
            oldChatConversation?.unReadCount ?: 0 + 1
        } else 0
        val conversation = ConversationEntity()
        conversation.userId = Account.userId
        conversation.targetId = targetId
        conversation.messageContent = messageContent
        conversation.messageType = messageType
        conversation.operationTime = operationTime
        conversation.unReadCount = unReadCount
        conversation.isAcceptMessage = isAcceptMessage
        conversation.sendStatus = sendStatus
        DataBaseManager.DB.conversationTableDao().insert(conversation)
    }

    /** 本地数据发送状态更新 */
    internal fun updateSendStatus(targetId: Long, sendSuccess: Boolean) {
        val status = if (sendSuccess)
            ChatMsgSendStatus.SendSuccess.value
        else
            ChatMsgSendStatus.SendFailed.value
        DataBaseManager.DB.conversationTableDao()
            .updateSendStatus(Account.userId, targetId, status)
    }
}