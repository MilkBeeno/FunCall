package com.milk.funcall.account.data

import com.milk.funcall.common.mdr.table.UserInfoEntity

data class FansModel(
    var current: Int = 0,
    var pages: Int = 0,
    var records: MutableList<UserInfoEntity>? = null
)