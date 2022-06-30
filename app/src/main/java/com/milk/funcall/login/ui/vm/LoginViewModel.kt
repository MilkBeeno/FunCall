package com.milk.funcall.login.ui.vm

import androidx.lifecycle.ViewModel
import com.milk.funcall.account.Account
import com.milk.funcall.common.author.AuthType
import com.milk.funcall.login.repo.LoginRepository
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
                loginRepository.login(currentDeviceId, authType, accessToken)
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                Account.logged(apiResult.accessToken)
                obtainUserInfo(apiResult.registeredFlag)
            } else failedRequest?.invoke()
        }
    }

    private suspend fun obtainUserInfo(registeredFlag: Boolean) {
        val apiResponse = loginRepository.obtainUserInfo()
        val apiResult = apiResponse.data
        if (apiResponse.success && apiResult != null) {
            if (registeredFlag)
                loginRequest?.invoke()
            else
                registerRequest?.invoke()
        } else failedRequest?.invoke()
    }
}