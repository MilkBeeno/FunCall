package com.milk.funcall.main.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milk.funcall.common.mdr.table.ChatMessageEntity
import com.milk.funcall.common.paging.LocalPagingSource
import com.milk.funcall.main.repo.ChatMessageRepository

class MessageViewModel : ViewModel() {
    var targetId: Long = 0
    val pagingSource: LocalPagingSource<Int, ChatMessageEntity>
        get() {
            return LocalPagingSource(
                pageSize = 20,
                prefetchDistance = 5,
                pagingSourceFactory = {
                    ChatMessageRepository.obtainMessages(targetId)
                }
            )
        }

    fun updateTargetUser(userId: Long) {
        targetId = userId
    }
}