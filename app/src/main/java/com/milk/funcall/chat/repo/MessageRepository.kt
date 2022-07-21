package com.milk.funcall.chat.repo

import androidx.paging.PagingSource
import com.milk.funcall.account.Account
import com.milk.funcall.chat.api.ApiService
import com.milk.funcall.chat.ui.type.ChatMessageType
import com.milk.funcall.chat.ui.type.ChatMsgSendStatus
import com.milk.funcall.common.mdr.DataBaseManager
import com.milk.funcall.common.mdr.table.ChatMessageEntity
import com.milk.funcall.common.mdr.table.ConversationEntity
import com.milk.funcall.common.net.retrofit
import com.milk.simple.ktx.ioScope
import com.milk.simple.log.Logger

object MessageRepository {
    private val chatMessageRepository by lazy { ChatMessageRepository() }
    private val conversationRepository by lazy { ConversationRepository() }

    /** 心跳包数据验证、检测是否有新的消息 */
    fun heartBeat() {
        ioScope {
            val apiResponse = retrofit {
                ApiService.chatApiService.heartBeat()
            }
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
        val apiResponse =
            retrofit { ApiService.chatApiService.getMessagesFromNetwork() }
        val apiResult = apiResponse.data
        if (apiResponse.success && apiResult != null) {
            apiResult.forEach { chatMsgReceiveModel ->
                chatMsgReceiveModel.chatMsgReceiveListModel
                    ?.forEach { chatMsgReceiveSingleModel ->
                        chatMessageRepository.saveTextMessage(chatMsgReceiveSingleModel.content) {
                            chatMessageRepository.receiveChatMessageEntity(
                                chatMsgReceiveSingleModel.itemId,
                                chatMsgReceiveModel.faceUserId,
                                ChatMessageType.TextReceived.value,
                                chatMsgReceiveSingleModel.chatTime
                            )
                        }
                        conversationRepository.saveConversation(
                            chatMsgReceiveModel.faceUserId,
                            ChatMessageType.TextReceived.value,
                            chatMsgReceiveSingleModel.chatTime,
                            isAcceptMessage = true,
                            ChatMsgSendStatus.SendSuccess.value
                        )
                    }
            }
        }
    }

    /** 发送文本私聊消息到服务器中 */
    suspend fun sendTextChatMessage(targetId: Long, messageContent: String) = retrofit {
        val messageUniqueId =
            chatMessageRepository.createMsgLocalUniqueId(Account.userId, targetId)
        val operationTime = System.currentTimeMillis()
        chatMessageRepository.saveTextMessage(messageContent) {
            chatMessageRepository.sendChatMessageEntity(
                messageUniqueId,
                targetId,
                ChatMessageType.TextSend.value,
                operationTime
            )
        }
        conversationRepository.saveConversation(
            targetId,
            ChatMessageType.TextSend.value,
            operationTime,
            isAcceptMessage = true,
            ChatMsgSendStatus.Sending.value
        )
        val apiResponse =
            ApiService.chatApiService.sendTextChatMessage(targetId, messageContent)
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
    fun getChatMessagesByDB(targetId: Long): PagingSource<Int, ChatMessageEntity> {
        return DataBaseManager.DB.chatMessageTableDao()
            .getChats(Account.userId, targetId)
    }

    /** 获取数据库中存储的会话消息 */
    fun getChatConversationByDB(targetId: Long): PagingSource<Int, ConversationEntity> {
        return DataBaseManager.DB.conversationTableDao()
            .getList(Account.userId, targetId)
    }
}