package com.milk.funcall.mdr.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.milk.funcall.data.MessageEntity

@Dao
interface MessageTableDao {
    @Query("SELECT * FROM MessageTable WHERE userId=:userId AND targetId=:targetId")
    fun obtainMessages(userId: Long, targetId: Long): PagingSource<Int, MessageEntity>

    @Insert
    fun insertMessage(messageEntityList: MutableList<MessageEntity>)
}