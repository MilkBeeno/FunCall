package com.milk.funcall.login.ui.vm

import androidx.lifecycle.ViewModel
import com.milk.funcall.account.Account
import com.milk.funcall.common.author.AuthType
import com.milk.funcall.login.repo.LoginRepository
import com.milk.funcall.user.repo.AccountRepository
import com.milk.simple.ktx.ioScope

class LoginViewModel : ViewModel() {
    private val loginRepository by lazy { LoginRepository() }
    var agreementPrivacy: Boolean = true
    var currentDeviceId: String = ""
    var loginRequest: (() -> Unit)? = null
    var registerRequest: (() -> Unit)? = null
    var failedRequest: (() -> Unit)? = null

    fun login(authType: AuthType, accessToken: String) {
        ioScope {
            val apiResponse =
                loginRepository.login(currentDeviceId, authType, accessToken + "4138743143")
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                Account.logged(apiResult.accessToken)
                if (apiResult.registeredFlag) {
                    loginRequest?.invoke()
                    // 登录成功后 1.老用户直接获取用户信息 2.新用户去预设头像名字信息后获取用户信息
                    AccountRepository.getAccountInfo(true)
                } else registerRequest?.invoke()
            } else failedRequest?.invoke()
        }
    }
}