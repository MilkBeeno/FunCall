package com.milk.funcall.common.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.milk.funcall.common.db.table.ChatMessageEntity

@Dao
interface ChatMessageTableDao {

    @Query("SELECT * FROM ChatMessageTable WHERE chatAccountId=:accountId AND chatTargetId=:targetId")
    fun getChatMessages(accountId: Long, targetId: Long): PagingSource<Int, ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chatMessageEntity: ChatMessageEntity)

    @Query("UPDATE ChatMessageTable SET chatNetworkMsgUniqueId=:msgNetworkUniqueId WHERE chatLocalMsgUniqueId=:msgLocalUniqueId")
    fun updateNetworkUniqueId(msgLocalUniqueId: String, msgNetworkUniqueId: String)

    @Query("UPDATE ChatMessageTable SET chatSendStatus=:sendStatus WHERE chatLocalMsgUniqueId=:msgLocalUniqueId")
    fun updateSendStatus(msgLocalUniqueId: String, sendStatus: Int)

    @Query("DELETE FROM ChatMessageTable WHERE chatAccountId=:accountId AND chatTargetId=:targetId")
    fun deleteChatMessage(accountId: Long, targetId: Long)
}