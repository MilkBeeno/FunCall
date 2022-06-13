package com.milk.funcall.mdr

import androidx.room.Database
import androidx.room.RoomDatabase
import com.milk.funcall.data.MessageEntity
import com.milk.funcall.mdr.dao.MessageTableDao

@Database(
    entities = [MessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun messageTableDao(): MessageTableDao
}