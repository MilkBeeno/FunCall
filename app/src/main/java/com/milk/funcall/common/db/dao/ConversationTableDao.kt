package com.milk.funcall.common.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.milk.funcall.common.db.table.ConversationEntity
import com.milk.funcall.common.db.table.ConversationWithUserInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationTableDao {

    @Query("SELECT * FROM ConversationTable WHERE conversationAccountId=:accountId AND conversationTargetId=:targetId")
    fun query(accountId: Long, targetId: Long): ConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chatConversationEntity: ConversationEntity)

    @Query("SELECT * FROM ConversationTable LEFT JOIN UserInfoTable ON UserInfoTable.userInfoTargetId = ConversationTable.conversationTargetId WHERE ConversationTable.conversationAccountId = :accountId ORDER BY ConversationTable.conversationPutTopTime DESC,ConversationTable.conversationOperationTime DESC")
    fun getConversations(accountId: Long): PagingSource<Int, ConversationWithUserInfoEntity>

    @Query("UPDATE ConversationTable SET conversationSendStatus=:sendStatus WHERE conversationAccountId=:accountId AND conversationTargetId=:targetId ")
    fun updateSendStatus(accountId: Long, targetId: Long, sendStatus: Int)

    @Query("UPDATE ConversationTable SET conversationUnReadCount=:unReadCount WHERE conversationAccountId=:accountId AND conversationTargetId=:targetId ")
    fun updateUnReadCount(accountId: Long, targetId: Long, unReadCount: Int = 0)

    @Query("DELETE FROM ConversationTable WHERE conversationAccountId=:accountId AND conversationTargetId=:targetId")
    fun deleteConversation(accountId: Long, targetId: Long)

    @Query("UPDATE ConversationTable SET conversationPutTopTime=:putTopTime WHERE conversationAccountId=:accountId AND conversationTargetId=:targetId")
    fun updatePutTopTime(accountId: Long, targetId: Long, putTopTime: Long)

    @Query("SELECT ConversationTable.conversationPutTopTime FROM ConversationTable WHERE conversationAccountId=:accountId AND conversationTargetId=:targetId LIMIT 1")
    fun getConversationPutTopTime(accountId: Long, targetId: Long): Long?

    @Query("SELECT ConversationTable.conversationUnReadCount FROM ConversationTable WHERE conversationAccountId=:accountId")
    fun getConversationCount(accountId: Long): Flow<MutableList<Int>?>
}