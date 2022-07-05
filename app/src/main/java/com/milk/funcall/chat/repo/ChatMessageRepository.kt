package com.milk.funcall.chat.repo

import com.milk.funcall.account.Account
import com.milk.funcall.common.mdr.DataBaseManager
import com.milk.funcall.common.mdr.table.ChatMessageEntity

class ChatMessageRepository {

    fun insertMessage(messageEntities: MutableList<ChatMessageEntity>) {
        DataBaseManager.DB.chatMessageTableDao().insertMessage(messageEntities)
    }

    fun obtainMessages(targetId: Long) =
        DataBaseManager.DB.chatMessageTableDao()
            .obtainMessages(Account.userId, targetId)

    fun createUid(userId: Long, targetId: Long) =
        System.currentTimeMillis().toString()
            .plus("-")
            .plus(userId)
            .plus("-")
            .plus(targetId)
}