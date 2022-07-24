package com.milk.funcall.common.mdr.table

import androidx.room.Embedded

data class ConversationWithUserInfoEntity(
    @Embedded
    var conversation: ConversationEntity,
    @Embedded
    var userInfo: UserInfoEntity?
)