package com.milk.funcall.common.mdr.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.milk.funcall.common.mdr.table.UserInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserInfoTableDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userInfoEntity: UserInfoEntity)

    @Query("SELECT * FROM UserInfoTable WHERE userInfoTargetId=:targetId LIMIT 1 ")
    fun query(targetId: Long): Flow<UserInfoEntity?>
}