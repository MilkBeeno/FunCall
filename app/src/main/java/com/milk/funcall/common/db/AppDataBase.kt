package com.milk.funcall.common.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.milk.funcall.common.db.dao.ChatMessageTableDao
import com.milk.funcall.common.db.dao.ConversationTableDao
import com.milk.funcall.common.db.dao.UserInfoTableDao
import com.milk.funcall.common.db.table.ChatMessageEntity
import com.milk.funcall.common.db.table.ConversationEntity
import com.milk.funcall.common.db.table.UserInfoEntity

@Database(
    entities = [ChatMessageEntity::class, UserInfoEntity::class, ConversationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun chatMessageTableDao(): ChatMessageTableDao
    abstract fun conversationTableDao(): ConversationTableDao
    abstract fun userInfoTableDao(): UserInfoTableDao
}