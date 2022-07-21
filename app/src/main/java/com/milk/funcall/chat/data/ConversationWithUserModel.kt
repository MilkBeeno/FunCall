package com.milk.funcall.chat.data

import androidx.room.Embedded
import com.milk.funcall.common.mdr.table.ConversationEntity
import com.milk.funcall.common.mdr.table.UserInfoEntity

data class ConversationWithUserInfo(
    @Embedded
    var conversation: ConversationEntity,
    @Embedded
    var userInfo: UserInfoEntity?
)