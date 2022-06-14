package com.milk.funcall.common.mdr.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.milk.funcall.main.data.ChatMessageEntity

@Dao
interface ChatMessageTableDao {

    @Query("SELECT * FROM ChatMessageTable WHERE userId=:userId AND targetId=:targetId")
    fun obtainMessages(userId: Long, targetId: Long): PagingSource<Int, ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(chatMessageEntities: MutableList<ChatMessageEntity>)
}