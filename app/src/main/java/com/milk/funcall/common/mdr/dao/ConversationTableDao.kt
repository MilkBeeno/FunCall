package com.milk.funcall.common.mdr.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.milk.funcall.common.mdr.table.ConversationWithUserInfoEntity
import com.milk.funcall.common.mdr.table.ConversationEntity

@Dao
interface ConversationTableDao {

    @Query("SELECT * FROM ConversationTable WHERE conversationAccountId=:accountId AND conversationTargetId=:targetId")
    fun query(accountId: Long, targetId: Long): ConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chatConversationEntity: ConversationEntity)

    @Query("SELECT * FROM ConversationTable LEFT JOIN UserInfoTable ON UserInfoTable.userInfoTargetId = ConversationTable.conversationTargetId WHERE ConversationTable.conversationAccountId = :accountId ORDER BY ConversationTable.conversationOperationTime DESC")
    fun getConversations(accountId: Long): PagingSource<Int, ConversationWithUserInfoEntity>

    @Query("UPDATE ConversationTable SET conversationSendStatus=:sendStatus WHERE conversationAccountId=:accountId AND conversationTargetId=:targetId ")
    fun updateSendStatus(accountId: Long, targetId: Long, sendStatus: Int)

    @Query("UPDATE ConversationTable SET conversationUnReadCount=:unReadCount WHERE conversationAccountId=:accountId AND conversationTargetId=:targetId ")
    fun updateUnReadCount(accountId: Long, targetId: Long, unReadCount: Int = 0)

    @Query("DELETE FROM ConversationTable WHERE conversationAccountId=:accountId AND conversationTargetId=:targetId")
    fun deleteConversation(accountId: Long, targetId: Long)
}