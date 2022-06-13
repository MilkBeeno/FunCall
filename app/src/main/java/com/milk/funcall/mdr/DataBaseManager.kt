package com.milk.funcall.mdr

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.milk.simple.ktx.ioScope
import kotlinx.coroutines.delay

object DataBaseManager {
    lateinit var DB: AppDataBase
    private const val DATA_BASE_NAME = "funcall.db"

    fun initializeDataBase(context: Application) {
        ioScope {
            delay(100)
            DB = Room.databaseBuilder(context, AppDataBase::class.java, DATA_BASE_NAME)
                //.addMigrations(MIGRATION_1_2)
                .build()
        }
    }

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
        }
    }
}