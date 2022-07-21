package com.milk.funcall.chat.ui.vm

import androidx.lifecycle.ViewModel
import com.milk.funcall.chat.data.ConversationWithUserInfo
import com.milk.funcall.chat.repo.MessageRepository
import com.milk.funcall.common.paging.LocalPagingSource

class ConversationViewModel : ViewModel() {
    val pagingSource: LocalPagingSource<Int, ConversationWithUserInfo>
        get() {
            return LocalPagingSource(
                pageSize = 20,
                prefetchDistance = 5,
                pagingSourceFactory = {
                    MessageRepository.getChatConversationByDB()
                }
            )
        }
}