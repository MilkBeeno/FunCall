package com.milk.funcall.common.mdr.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.milk.funcall.common.mdr.table.UserInfoEntity

@Dao
interface UserInfoTableDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userInfoEntity: UserInfoEntity)
}