package com.milk.funcall.common.mdr.table

import androidx.room.Embedded

data class ConversationWithUserInfoEntity(
    @Embedded
    var conversation: ConversationEntity? = null,
    @Embedded
    var userInfo: UserInfoEntity? = null
)