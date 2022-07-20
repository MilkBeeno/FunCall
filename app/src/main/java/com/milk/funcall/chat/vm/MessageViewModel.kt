package com.milk.funcall.chat.vm

import androidx.lifecycle.ViewModel
import com.milk.funcall.chat.repo.ChatMessageRepository
import com.milk.funcall.common.mdr.table.ChatMessageEntity
import com.milk.funcall.common.paging.LocalPagingSource

class MessageViewModel : ViewModel() {
    var targetId: Long = 0
    val pagingSource: LocalPagingSource<Int, ChatMessageEntity>
        get() {
            return LocalPagingSource(
                pageSize = 20,
                prefetchDistance = 5,
                pagingSourceFactory = {
                    ChatMessageRepository.getChatMessagesByDB(targetId)
                }
            )
        }

    fun updateTargetUser(userId: Long) {
        targetId = userId
    }
}