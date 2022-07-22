package com.milk.funcall.chat.ui.vm

import androidx.lifecycle.ViewModel
import com.milk.funcall.chat.repo.MessageRepository
import com.milk.funcall.common.mdr.table.ConversationWithUserInfoEntity
import com.milk.funcall.common.paging.LocalPagingSource
import com.milk.simple.ktx.ioScope

class ConversationViewModel : ViewModel() {
    val pagingSource: LocalPagingSource<Int, ConversationWithUserInfoEntity>
        get() {
            return LocalPagingSource(
                pageSize = 20,
                prefetchDistance = 5,
                pagingSourceFactory = {
                    MessageRepository.getChatConversationByDB()
                }
            )
        }

    fun deleteChatMessage(targetId: Long) {
        ioScope { MessageRepository.deleteChatMessage(targetId) }
    }
}