package com.milk.funcall.login.ui.vm

import androidx.lifecycle.ViewModel
import com.milk.funcall.common.author.AuthType
import com.milk.funcall.login.repo.LoginRepository
import com.milk.funcall.main.Account
import com.milk.simple.ktx.ioScope

class LoginViewModel : ViewModel() {
    var agreementPrivacy: Boolean = true
    var currentDeviceId: String = ""
    var loginRequest: (() -> Unit)? = null
    var registerRequest: (() -> Unit)? = null
    var failedRequest: (() -> Unit)? = null

    fun login(authType: AuthType, accessToken: String) {
        ioScope {
            val apiResponse =
                LoginRepository.login(currentDeviceId, authType, accessToken)
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                Account.logged(apiResult.accessToken)
                if (apiResult.registeredFlag)
                    loginRequest?.invoke()
                else
                    registerRequest?.invoke()
            } else failedRequest?.invoke()
        }
    }
}