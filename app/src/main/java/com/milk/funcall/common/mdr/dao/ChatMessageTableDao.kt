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
    fun getChats(userId: Long, targetId: Long): PagingSource<Int, ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chatMessageEntity: ChatMessageEntity)

    @Query("UPDATE ChatMessageTable SET chatNetworkMsgUniqueId=:msgNetworkUniqueId WHERE chatLocalMsgUniqueId=:msgLocalUniqueId")
    fun updateNetworkUniqueId(msgLocalUniqueId: String, msgNetworkUniqueId: String)

    @Query("UPDATE ChatMessageTable SET chatSendStatus=:sendStatus WHERE chatLocalMsgUniqueId=:msgLocalUniqueId")
    fun updateSendStatus(msgLocalUniqueId: String, sendStatus: Int)
}