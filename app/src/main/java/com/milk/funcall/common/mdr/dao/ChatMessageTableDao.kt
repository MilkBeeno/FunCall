package com.milk.funcall.common.mdr.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.milk.funcall.common.mdr.table.ChatMessageEntity

@Dao
interface ChatMessageTableDao {

    @Query("SELECT * FROM ChatMessageTable WHERE chatUserId=:userId AND chatTargetId=:targetId")
    fun getChatMessages(userId: Long, targetId: Long): PagingSource<Int, ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(chatMessageEntity: ChatMessageEntity)
}