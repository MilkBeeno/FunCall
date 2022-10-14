package com.milk.funcall.common.db.table

import androidx.room.Embedded

data class ConversationWithUserInfoEntity(
    @Embedded
    var conversation: ConversationEntity,
    @Embedded
    var userInfo: UserInfoEntity?
)