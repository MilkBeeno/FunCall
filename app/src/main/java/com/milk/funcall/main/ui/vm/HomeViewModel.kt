package com.milk.funcall.main.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milk.funcall.common.data.MessageEntity
import com.milk.funcall.main.repository.MessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    fun addMessageData() {
        viewModelScope.launch(Dispatchers.IO) {
            val msg = mutableListOf<MessageEntity>()
            msg.add(MessageEntity(messageUniqueId = "这是第几个$1", userId = 123, targetId = 1234))
            msg.add(MessageEntity(messageUniqueId = "这是第几个$2", userId = 123, targetId = 1234))
            msg.add(MessageEntity(messageUniqueId = "这是第几个$3", userId = 123, targetId = 1234))
            msg.add(MessageEntity(messageUniqueId = "这是第几个$4", userId = 123, targetId = 1234))
            msg.add(MessageEntity(messageUniqueId = "这是第几个$5", userId = 123, targetId = 1234))
            MessageRepository.insertMessage(msg)
        }
    }
}