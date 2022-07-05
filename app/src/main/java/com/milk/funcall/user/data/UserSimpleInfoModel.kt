package com.milk.funcall.user.data

import com.milk.funcall.common.mdr.table.UserInfoEntity

/** @param isMediumImage: 本地用来判断是否是小图 */
data class UserSimpleInfoModel(var isMediumImage: Boolean = false) : UserInfoEntity()