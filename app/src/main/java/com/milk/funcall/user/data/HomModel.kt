package com.milk.funcall.user.data

data class HomModel(
    // 请求分页页码
    val groupNumber: Int = 0,
    // 数据总页数
    val pages: Int = 0,
    // 每页最大显示数据条数
    val size: Int = 0,
    // 列表一共有多少条数据
    val total: Int = 0,
    // 真实的数据
    val records: MutableList<UserSimpleInfoModel>? = null
)