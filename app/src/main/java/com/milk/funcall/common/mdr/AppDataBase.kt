package com.milk.funcall.common.mdr

import androidx.room.Database
import androidx.room.RoomDatabase
import com.milk.funcall.common.mdr.dao.ChatMessageTableDao
import com.milk.funcall.common.mdr.table.ChatMessageEntity

@Database(
    entities = [ChatMessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun chatMessageTableDao(): ChatMessageTableDao
}