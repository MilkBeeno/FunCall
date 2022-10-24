package com.milk.funcall.chat.repo

import com.milk.funcall.account.Account
import com.milk.funcall.chat.status.ChatMsgSendStatus
import com.milk.funcall.common.db.DataBaseManager
import com.milk.funcall.common.db.table.ConversationEntity
import com.milk.simple.ktx.safeToLong

/** MessageFragment 页面会话列表数据 */
class ConversationRepository {

    /** 会话列表数据表 */
    internal fun saveConversation(
        targetId: Long,
        targetName: String,
        targetAvatar: String,
        messageType: Int,
        operationTime: Long,
        isAcceptMessage: Boolean,
        sendStatus: Int,
        messageContent: String = ""
    ) {
        val oldChatConversation =
            DataBaseManager.DB.conversationTableDao().query(Account.userId, targetId)
        val unReadCount = if (isAcceptMessage) (oldChatConversation?.unReadCount ?: 0) + 1 else 0
        val putTopTime = oldChatConversation?.putTopTime.safeToLong()
        val conversation = ConversationEntity()
        conversation.accountId = Account.userId
        conversation.targetId = targetId
        conversation.targetName = targetName
        conversation.targetAvatar = targetAvatar
        conversation.messageContent = messageContent
        conversation.messageType = messageType
        conversation.operationTime = operationTime
        conversation.putTopTime = putTopTime
        conversation.unReadCount = unReadCount
        conversation.isAcceptMessage = isAcceptMessage
        conversation.sendStatus = sendStatus
        DataBaseManager.DB.conversationTableDao().insert(conversation)
    }

    /** 本地数据发送状态更新 */
    internal fun updateSendStatus(targetId: Long, sendSuccess: Boolean) {
        val status = if (sendSuccess) {
            ChatMsgSendStatus.SendSuccess.value
        } else {
            ChatMsgSendStatus.SendFailed.value
        }
        DataBaseManager.DB.conversationTableDao().updateSendStatus(Account.userId, targetId, status)
    }
}