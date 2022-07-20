package com.milk.funcall.chat.ui.vm

import androidx.lifecycle.ViewModel
import com.milk.funcall.chat.repo.ChatMessageRepository
import com.milk.funcall.common.mdr.table.ChatMessageEntity
import com.milk.funcall.common.paging.LocalPagingSource
import com.milk.simple.ktx.ioScope

class ChatMessageViewModel : ViewModel() {
    private val chatMessageRepository by lazy { ChatMessageRepository() }
    private var targetId: Long = 0
    val pagingSource: LocalPagingSource<Int, ChatMessageEntity>
        get() {
            return LocalPagingSource(
                pageSize = 20,
                prefetchDistance = 5,
                pagingSourceFactory = {
                    chatMessageRepository.getChatMessagesByDB(targetId)
                }
            )
        }

    fun updateTargetUser(userId: Long) {
        targetId = userId
    }

    fun sendTextChatMessage(messageContent: String) {
        ioScope { chatMessageRepository.sendTextChatMessage(targetId, messageContent) }
    }
}