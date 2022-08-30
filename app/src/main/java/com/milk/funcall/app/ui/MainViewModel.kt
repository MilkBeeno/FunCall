package com.milk.funcall.app.ui

import androidx.lifecycle.ViewModel
import com.milk.funcall.chat.repo.MessageRepository

class MainViewModel : ViewModel() {
    internal fun getConversationCount() = MessageRepository.getConversationCount()
}