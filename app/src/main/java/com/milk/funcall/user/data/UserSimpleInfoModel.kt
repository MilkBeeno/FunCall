package com.milk.funcall.user.data

import com.milk.funcall.common.db.table.UserInfoEntity
import com.milk.funcall.user.status.ItemAdType

/** @param isMediumImage: 本地用来判断是否是小图 */
data class UserSimpleInfoModel(
    var isMediumImage: Boolean = false,
    var itemAdType: ItemAdType = ItemAdType.Null
) : UserInfoEntity()