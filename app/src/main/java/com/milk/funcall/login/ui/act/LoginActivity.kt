package com.milk.funcall.login.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.milk.funcall.R
import com.milk.funcall.app.ui.act.MainActivity
import com.milk.funcall.common.author.AuthLoginManager
import com.milk.funcall.common.author.AuthType
import com.milk.funcall.common.author.DeviceNumber
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.common.web.WebActivity
import com.milk.funcall.databinding.ActivityLoginBinding
import com.milk.funcall.login.ui.dialog.LoadingDialog
import com.milk.funcall.login.ui.vm.LoginViewModel
import com.milk.simple.ktx.*

class LoginActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityLoginBinding>()
    private val loginViewModel by viewModels<LoginViewModel>()
    private val authLoginManager by lazy { AuthLoginManager(this) }
    private val loadingDialog by lazy { LoadingDialog(this) }
    private var isNotAuthorizing: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        initializeCallback()
    }

    private fun initializeView() {
        immersiveStatusBar()
        binding.root.navigationBarPadding()
        binding.llGoogle.setOnClickListener(this)
        binding.llFacebook.setOnClickListener(this)
        binding.llDevice.setOnClickListener(this)
        binding.ivPrivacyCheck.setOnClickListener(this)
        binding.tvPrivacy.text = string(R.string.login_privacy_desc)
        binding.tvPrivacy.setSpannableClick(
            Pair(
                string(R.string.login_user_agreement),
                colorClickableSpan(color(R.color.FF8E58FB)) {
                    WebActivity.create(this)
                }),
            Pair(
                string(R.string.login_user_privacy),
                colorClickableSpan(color(R.color.FF8E58FB)) {
                    WebActivity.create(this)
                })
        )
    }

    private fun initializeCallback() {
        authLoginManager.success = { type, accessToken ->
            // Logger.d("获取的accessToken是=${accessToken}", "hlc")
            loadingDialog.show()
            loginViewModel.login(type, accessToken)
        }
        authLoginManager.cancel = { isNotAuthorizing = true }
        authLoginManager.failed = { isNotAuthorizing = true }
        loginViewModel.loginRequest = {
            finish()
            MainActivity.create(this)
        }
        loginViewModel.registerRequest = {
            finish()
            CreateNameActivity.create(this)
        }
        loginViewModel.failedRequest = {
            isNotAuthorizing = true
            loadingDialog.dismiss()
        }
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.llGoogle -> checkIsAllowedToLoginAuth {
                if (isNotAuthorizing) {
                    isNotAuthorizing = false
                    loginViewModel.currentDeviceId = it
                    authLoginManager.googleAuth()
                }
            }
            binding.llFacebook -> checkIsAllowedToLoginAuth {
                if (isNotAuthorizing) {
                    isNotAuthorizing = false
                    authLoginManager.facebookAuth()
                    loginViewModel.currentDeviceId = it
                }
            }
            binding.llDevice -> checkIsAllowedToLoginAuth {
                if (isNotAuthorizing) {
                    isNotAuthorizing = false
                    loginViewModel.currentDeviceId = it
                    authLoginManager.success?.invoke(AuthType.Device, it)
                }
            }
            binding.ivPrivacyCheck -> {
                loginViewModel.agreementPrivacy = !loginViewModel.agreementPrivacy
                binding.ivPrivacyCheck.setImageResource(
                    if (loginViewModel.agreementPrivacy)
                        R.drawable.login_privacy_checked
                    else
                        R.drawable.login_privacy_no_check
                )
            }
        }
    }

    private fun checkIsAllowedToLoginAuth(request: (String) -> Unit) {
        if (loginViewModel.agreementPrivacy) {
            DeviceNumber.obtain(this@LoginActivity) { success, deviceId ->
                if (success && deviceId.isNotBlank())
                    mainScope { request(deviceId) }
                else
                    showToast(string(R.string.login_obtain_device_failed))
            }
        } else showToast(string(R.string.login_check_privacy))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        authLoginManager.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        fun create(context: Context) =
            context.startActivity(Intent(context, LoginActivity::class.java))
    }
}