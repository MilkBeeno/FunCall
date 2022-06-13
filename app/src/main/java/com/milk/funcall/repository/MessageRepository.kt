package com.milk.funcall.repository

import com.milk.funcall.Account
import com.milk.funcall.data.MessageEntity
import com.milk.funcall.mdr.DataBaseManager

object MessageRepository {

    suspend fun insertMessage(messageEntityList: MutableList<MessageEntity>) {
        DataBaseManager.DB.messageTableDao().insertMessage(messageEntityList)
    }

    fun obtainMessages(targetId: Long) =
        DataBaseManager.DB.messageTableDao()
            .obtainMessages(Account.userId.value ?: 0, targetId)
}