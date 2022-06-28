package com.milk.funcall.login.data

data class LoginModel(
    // 登录的 AccessToken
    val accessToken: String = "",
    // 	是否已经注册( false 未注册 | true 已注册 )
    val registeredFlag: Boolean = false
)