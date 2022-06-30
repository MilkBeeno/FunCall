package com.milk.funcall.user.data

data class HomModel(
    val userId: Long = 0,
    val userAvatar: String,
    val userName: String = "",
    val userImage: String = "",
    val isOnline: Boolean = false,
    val isSmallImage: Boolean = false
)