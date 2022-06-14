package com.milk.funcall.main.repository

import com.milk.funcall.common.Account
import com.milk.funcall.common.data.MessageEntity
import com.milk.funcall.common.mdr.DataBaseManager

object MessageRepository {

    suspend fun insertMessage(messageEntityList: MutableList<MessageEntity>) {
        DataBaseManager.DB.messageTableDao().insertMessage(messageEntityList)
    }

    fun obtainMessages(targetId: Long) =
        DataBaseManager.DB.messageTableDao()
            .obtainMessages(Account.userId.value ?: 0, targetId)
}