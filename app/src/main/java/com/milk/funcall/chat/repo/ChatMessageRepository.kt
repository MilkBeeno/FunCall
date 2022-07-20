package com.milk.funcall.chat.repo

import androidx.paging.PagingSource
import com.milk.funcall.account.Account
import com.milk.funcall.chat.api.ApiService
import com.milk.funcall.chat.ui.type.ChatMessageType
import com.milk.funcall.chat.ui.type.ChatMsgSendStatus
import com.milk.funcall.common.mdr.DataBaseManager
import com.milk.funcall.common.mdr.table.ChatMessageEntity
import com.milk.funcall.common.net.retrofit
import com.milk.simple.ktx.ioScope
import com.milk.simple.log.Logger

object ChatMessageRepository {
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
                        saveTextChatMessage(
                            chatMsgReceiveSingleModel.itemId,
                            chatMsgReceiveModel.faceUserId,
                            chatMsgReceiveSingleModel.content,
                            chatMsgReceiveSingleModel.chatTime,
                            isAcceptMessage = true
                        )
                    }
            }
        }
    }

    /** 发送文本私聊消息到服务器中 */
    suspend fun sendTextChatMessage(targetId: Long, messageContent: String) = retrofit {
        saveTextChatMessage(
            createMsgUniqueId(Account.userId, targetId),
            targetId,
            messageContent,
            System.currentTimeMillis(),
            isAcceptMessage = false
        )
        val apiResponse =
            ApiService.chatApiService.sendTextChatMessage(targetId, messageContent)
        val apiResult = apiResponse.data
        val sendSuccessful = apiResponse.success && apiResult != null
        // 发送成功更改数据库中发送标志
        apiResponse
    }

    /** 保存文本消息到数据库中 */
    private fun saveTextChatMessage(
        messageUniqueId: String,
        targetId: Long,
        messageContent: String,
        operationTime: Long,
        isAcceptMessage: Boolean
    ) {
        val textChatMessageEntity =
            if (isAcceptMessage)
                receiveChatMessageEntity(
                    messageUniqueId,
                    targetId,
                    ChatMessageType.TextReceived.value,
                    operationTime
                )
            else
                sendChatMessageEntity(
                    messageUniqueId,
                    targetId,
                    ChatMessageType.TextSend.value,
                    operationTime
                )
        textChatMessageEntity.messageContent = messageContent
        insertMessage(textChatMessageEntity)
    }

    /** 获取消息唯一 ID */
    private fun createMsgUniqueId(userId: Long, targetId: Long) =
        System.currentTimeMillis().toString()
            .plus("-")
            .plus(userId)
            .plus("-")
            .plus(targetId)

    /** 网络请求结果私聊实体 */
    private fun receiveChatMessageEntity(
        messageUniqueId: String,
        targetId: Long,
        messageType: Int,
        operationTime: Long
    ) = ChatMessageEntity().apply {
        this.messageUniqueId = messageUniqueId
        this.userId = Account.userId
        this.targetId = targetId
        this.messageType = messageType
        this.operationTime = operationTime
        this.isReadMessage = false
        this.isAcceptMessage = true
        this.sendStatus = ChatMsgSendStatus.SendSuccess.value
    }

    /** 网络请求发送私聊实体 */
    private fun sendChatMessageEntity(
        messageUniqueId: String,
        targetId: Long,
        messageType: Int,
        operationTime: Long
    ) = ChatMessageEntity().apply {
        this.messageUniqueId = messageUniqueId
        this.userId = Account.userId
        this.targetId = targetId
        this.messageType = messageType
        this.operationTime = operationTime
        this.isReadMessage = true
        this.isAcceptMessage = false
        this.sendStatus = ChatMsgSendStatus.Sending.value
    }

    /** 向数据库中插入单条消息 */
    private fun insertMessage(messageEntity: ChatMessageEntity) {
        DataBaseManager.DB.chatMessageTableDao().insertMessage(messageEntity)
    }

    /** 获取数据库中存储的私聊消息 */
    fun getChatMessagesByDB(targetId: Long): PagingSource<Int, ChatMessageEntity> {
        return DataBaseManager.DB.chatMessageTableDao().getChatMessages(Account.userId, targetId)
    }
}