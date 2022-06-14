package com.milk.funcall.main.repository

import com.milk.funcall.common.Account
import com.milk.funcall.common.mdr.DataBaseManager
import com.milk.funcall.main.data.ChatMessageEntity

object MessageRepository {

    fun insertMessage(messageEntities: MutableList<ChatMessageEntity>) {
        DataBaseManager.DB.chatMessageTableDao().insertMessage(messageEntities)
    }

    fun obtainMessages(targetId: Long) =
        DataBaseManager.DB.chatMessageTableDao()
            .obtainMessages(Account.userId.value ?: 0, targetId)

    fun createUid(userId: Long, targetId: Long) =
        System.currentTimeMillis().toString()
            .plus("-")
            .plus(userId)
            .plus("-")
            .plus(targetId)
}