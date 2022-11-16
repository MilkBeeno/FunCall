package com.milk.funcall.chat.repo

import androidx.paging.PagingSource
import com.milk.funcall.account.Account
import com.milk.funcall.chat.api.ChatApiService
import com.milk.funcall.chat.status.ChatMessageType
import com.milk.funcall.chat.status.ChatMsgSendStatus
import com.milk.funcall.common.db.DataBaseManager
import com.milk.funcall.common.db.table.ChatMessageEntity
import com.milk.funcall.common.db.table.ConversationWithUserInfoEntity
import com.milk.funcall.common.net.ApiClient
import com.milk.funcall.common.net.retrofit
import com.milk.simple.ktx.ioScope
import com.milk.simple.log.Logger

object MessageRepository {
    private val chatApiService: ChatApiService =
        ApiClient.getMainRetrofit().create(ChatApiService::class.java)
    private val chatMessageRepository by lazy { ChatMessageRepository() }
    private val conversationRepository by lazy { ConversationRepository() }

    /** 心跳包数据验证、检测是否有新的消息 */
    internal fun heartBeat() {
        ioScope {
            val apiResponse = retrofit { chatApiService.heartBeat() }
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                Logger.d(
                    "IM Okhttp 心跳包返回成功、当前时间=${System.currentTimeMillis()}",
                    "IM-Service"
                )
                if (apiResult.messageNewsFlag) {
                    getChatMessageByNetwork()
                }
                if (apiResult.systemNewsFlag) {
                    // todo hlc : 有系统消息
                }
            }
        }
    }

    /** 从服务器端新消息数据、并保存到数据库中 */
    private suspend fun getChatMessageByNetwork() {
        val apiResponse = retrofit { chatApiService.getMessagesFromNetwork() }
        val apiResult = apiResponse.data
        if (apiResponse.success && apiResult != null) {
            apiResult.forEach { chatMsgReceiveModel ->
                chatMsgReceiveModel.chatMsgReceiveListModel
                    ?.forEach { chatMsgReceiveSingleModel ->
                        chatMessageRepository.saveTextMessage(chatMsgReceiveSingleModel.content) {
                            chatMessageRepository.receiveChatMessageEntity(
                                msgNetworkUniqueId = chatMsgReceiveSingleModel.itemId,
                                targetId = chatMsgReceiveModel.faceUserId,
                                targetName = chatMsgReceiveModel.faceNickname,
                                targetAvatar = chatMsgReceiveModel.faceAvatarUrl,
                                messageType = ChatMessageType.TextReceived.value,
                                operationTime = chatMsgReceiveSingleModel.chatTimeStamp
                            )
                        }
                        conversationRepository.saveConversation(
                            targetId = chatMsgReceiveModel.faceUserId,
                            targetName = chatMsgReceiveModel.faceNickname,
                            targetAvatar = chatMsgReceiveModel.faceAvatarUrl,
                            messageType = ChatMessageType.TextReceived.value,
                            operationTime = chatMsgReceiveSingleModel.chatTimeStamp,
                            isAcceptMessage = true,
                            sendStatus = ChatMsgSendStatus.SendSuccess.value,
                            messageContent = chatMsgReceiveSingleModel.content
                        )
                    }
            }
        }
    }

    /** 发送文本私聊消息到服务器中 */
    internal suspend fun sendTextChatMessage(
        targetId: Long,
        targetName: String,
        targetAvatar: String,
        messageContent: String
    ) = retrofit {
        val messageUniqueId =
            chatMessageRepository.createMsgLocalUniqueId(Account.userId, targetId)
        val operationTime = System.currentTimeMillis()
        chatMessageRepository.saveTextMessage(messageContent) {
            chatMessageRepository.sendChatMessageEntity(
                msgLocalUniqueId = messageUniqueId,
                targetId = targetId,
                targetName = targetName,
                targetAvatar = targetAvatar,
                messageType = ChatMessageType.TextSend.value,
                operationTime = operationTime
            )
        }
        conversationRepository.saveConversation(
            targetId = targetId,
            targetName = targetName,
            targetAvatar = targetAvatar,
            messageType = ChatMessageType.TextSend.value,
            operationTime = operationTime,
            isAcceptMessage = false,
            sendStatus = ChatMsgSendStatus.Sending.value,
            messageContent = messageContent
        )
        val apiResponse = chatApiService.sendTextChatMessage(targetId, messageContent)
        val apiResult = apiResponse.data
        // 消息发送状态更新、3.发送成功 2.发送失败
        if (apiResponse.success && apiResult != null) {
            chatMessageRepository
                .updateSendStatus(messageUniqueId, true, apiResult.itemId)
            conversationRepository.updateSendStatus(targetId, true)
        } else {
            chatMessageRepository
                .updateSendStatus(messageUniqueId, false)
            conversationRepository.updateSendStatus(targetId, false)
        }
        apiResponse
    }

    /** 获取数据库中存储的私聊消息 */
    internal fun getChatMessagesByDB(targetId: Long): PagingSource<Int, ChatMessageEntity> {
        return DataBaseManager.DB.chatMessageTableDao()
            .getChatMessages(Account.userId, targetId)
    }

    /** 获取数据库中存储的会话消息 */
    internal fun getChatConversationByDB(): PagingSource<Int, ConversationWithUserInfoEntity> {
        return DataBaseManager.DB.conversationTableDao().getConversations(Account.userId)
    }

    /** 更改数据库中存储的会话消息未读数量 */
    internal fun updateUnReadCount(targetId: Long) {
        DataBaseManager.DB.conversationTableDao().updateUnReadCount(Account.userId, targetId)
    }

    /** 删除某条消息 */
    internal fun deleteChatMessage(targetId: Long) {
        DataBaseManager.DB.conversationTableDao().deleteConversation(Account.userId, targetId)
        DataBaseManager.DB.chatMessageTableDao().deleteChatMessage(Account.userId, targetId)
    }

    /** 将某条消息置顶 */
    internal fun putTopChatMessage(targetId: Long) {
        DataBaseManager.DB.conversationTableDao()
            .updatePutTopTime(Account.userId, targetId, System.currentTimeMillis())
    }

    /** 将某条消息取消置顶 */
    internal fun unPinChatMessage(targetId: Long) {
        DataBaseManager.DB.conversationTableDao()
            .updatePutTopTime(Account.userId, targetId, 0)
    }

    /** 获取当前用户的置顶状态 */
    internal fun getConversationPutTopTime(targetId: Long) =
        DataBaseManager.DB.conversationTableDao()
            .getConversationPutTopTime(Account.userId, targetId)

    /** 获取当前用户未读消息数量 */
    internal fun getConversationCount() =
        DataBaseManager.DB.conversationTableDao()
            .getConversationCount(Account.userId)
}