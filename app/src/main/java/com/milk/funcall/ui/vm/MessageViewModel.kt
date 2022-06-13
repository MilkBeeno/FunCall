package com.milk.funcall.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milk.common.paging.DBPagingSource
import com.milk.funcall.repository.MessageRepository

class MessageViewModel : ViewModel() {
    fun obtainPagingSource(targetId: Long) = DBPagingSource(
        viewModelScope,
        pageSize = 20,
        prefetchDistance = 5,
        pagingSourceFactory = { MessageRepository.obtainMessages(targetId) }
    )
}