package com.milk.funcall.login.ui.vm

import androidx.lifecycle.ViewModel
import com.milk.funcall.account.Account
import com.milk.funcall.account.repo.AccountRepository
import com.milk.funcall.common.author.AuthType
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.pay.PayManager
import com.milk.funcall.login.repo.LoginRepository
import com.milk.simple.ktx.ioScope

class LoginViewModel : ViewModel() {
    private val loginRepository by lazy { LoginRepository() }
    var agreementPrivacy: Boolean = true
    var currentDeviceId: String = ""
    var loginRequest: (() -> Unit)? = null
    var registerRequest: (() -> Unit)? = null
    var failedRequest: ((Int) -> Unit)? = null

    internal fun login(authType: AuthType, accessToken: String) {
        ioScope {
            val apiResponse =
                loginRepository.login(currentDeviceId, authType, accessToken)
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                when (authType) {
                    AuthType.Google ->
                        FireBaseManager.logEvent(FirebaseKey.LOGINS_WITH_GOOGLE_SUCCESS)
                    AuthType.Facebook ->
                        FireBaseManager.logEvent(FirebaseKey.LOGINS_WITH_FB_SUCCESS)
                    AuthType.Device ->
                        FireBaseManager.logEvent(FirebaseKey.LOGINS_WITH_GUEST_SUCCESS)
                }
                Account.logged(apiResult.accessToken)
                if (apiResult.registeredFlag) {
                    FireBaseManager.logEvent(FirebaseKey.LOGIN_SUCCESSFUL)
                    loginRequest?.invoke()
                    // 登录成功后 1.老用户直接获取用户信息 2.新用户去预设头像名字信息后获取用户信息
                    AccountRepository.getAccountInfo(true)
                    // 登录之后去获取 vip 的状态
                    PayManager.getPayStatus()
                } else registerRequest?.invoke()
            } else {
                when (authType) {
                    AuthType.Google ->
                        FireBaseManager.logEvent(FirebaseKey.LOGINS_WITH_GOOGLE_FAIL)
                    AuthType.Facebook ->
                        FireBaseManager.logEvent(FirebaseKey.LOGINS_WITH_FB_FAIL)
                    AuthType.Device ->
                        FireBaseManager.logEvent(FirebaseKey.LOGINS_WITH_GUEST_FAIL)
                }
                FireBaseManager.logEvent(FirebaseKey.LOGIN_FAIL)
                failedRequest?.invoke(apiResponse.code)
            }
        }
    }
}