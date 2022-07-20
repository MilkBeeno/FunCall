package com.milk.funcall.common.mdr.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.milk.funcall.common.mdr.table.ChatConversationEntity

@Dao
interface ChatConversationTableDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(chatConversationEntity: ChatConversationEntity)
}