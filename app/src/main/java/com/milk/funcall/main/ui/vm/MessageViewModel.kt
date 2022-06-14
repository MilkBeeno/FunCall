package com.milk.funcall.main.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milk.funcall.common.paging.SimplePagingSource
import com.milk.funcall.main.data.ChatMessageEntity
import com.milk.funcall.main.repository.MessageRepository

class MessageViewModel : ViewModel() {
    var targetId: Long = 0
    val pagingSource: SimplePagingSource<Int, ChatMessageEntity>
        get() {
            return SimplePagingSource(
                viewModelScope,
                pageSize = 20,
                prefetchDistance = 5,
                pagingSourceFactory = {
                    MessageRepository.obtainMessages(targetId)
                }
            )
        }

    fun updateTargetUser(userId: Long) {
        targetId = userId
    }
}