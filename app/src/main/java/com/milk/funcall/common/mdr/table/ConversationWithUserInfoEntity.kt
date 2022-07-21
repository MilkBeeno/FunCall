package com.milk.funcall.common.mdr.table

import androidx.room.Embedded
import com.milk.funcall.common.mdr.table.ConversationEntity
import com.milk.funcall.common.mdr.table.UserInfoEntity

data class ConversationWithUserInfoEntity(
    @Embedded
    var conversation: ConversationEntity,
    @Embedded
    var userInfo: UserInfoEntity?
)