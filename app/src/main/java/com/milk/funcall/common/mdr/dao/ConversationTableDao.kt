package com.milk.funcall.common.mdr.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.milk.funcall.common.mdr.table.ConversationEntity

@Dao
interface ConversationTableDao {

    @Query("SELECT * FROM ConversationTable WHERE conversationUserId=:userId AND conversationTargetId=:targetId")
    fun query(userId: Long, targetId: Long): ConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chatConversationEntity: ConversationEntity)

    @Query("SELECT * FROM ConversationTable WHERE conversationUserId=:userId")
    fun getConversations(userId: Long): PagingSource<Int, ConversationEntity>

    @Query("UPDATE ConversationTable SET conversationSendStatus=:sendStatus WHERE conversationUserId=:userId AND conversationTargetId=:targetId ")
    fun updateSendStatus(userId: Long, targetId: Long, sendStatus: Int)

    @Query("UPDATE ConversationTable SET conversationUnReadCount=:unReadCount WHERE conversationUserId=:userId AND conversationTargetId=:targetId ")
    fun updateUnReadCount(userId: Long, targetId: Long, unReadCount: Int = 0)
}