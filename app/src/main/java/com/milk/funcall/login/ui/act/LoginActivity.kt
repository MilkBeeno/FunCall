package com.milk.funcall.login.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.milk.funcall.R
import com.milk.funcall.common.author.AuthLoginManager
import com.milk.funcall.common.author.AuthType
import com.milk.funcall.common.author.DeviceNumber
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.common.web.WebActivity
import com.milk.funcall.databinding.ActivityLoginBinding
import com.milk.funcall.login.ui.vm.LoginViewModel
import com.milk.funcall.main.ui.act.MainActivity
import com.milk.simple.ktx.*

class LoginActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityLoginBinding>()
    private val loginViewModel by viewModels<LoginViewModel>()
    private val authLoginManager = AuthLoginManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        immersiveStatusBar()
        initializeView()
        initializeCallback()
    }

    private fun initializeView() {
        binding.llGoogle.setOnClickListener(this)
        binding.llFacebook.setOnClickListener(this)
        binding.llDevice.setOnClickListener(this)
        binding.ivPrivacyCheck.setOnClickListener(this)

        binding.tvPrivacy.text = string(R.string.login_privacy_desc)
        binding.tvPrivacy.setSpannableClick(
            Pair(
                string(R.string.login_user_agreement),
                colorClickableSpan(color(R.color.FF8E58FB)) { WebActivity.create(this) }),
            Pair(
                string(R.string.login_user_privacy),
                colorClickableSpan(color(R.color.FF8E58FB)) { WebActivity.create(this) })
        )
    }

    private fun initializeCallback() {
        authLoginManager.success = { type, accessToken ->
            //Logger.d("获取的accessToken是=${accessToken}", "hlc")
            loginViewModel.login(type, accessToken)
        }
        loginViewModel.loginRequest = {
            finish()
            MainActivity.create(this)
        }
        loginViewModel.registerRequest = {
            finish()
            CreateNameActivity.create(this)
        }
        loginViewModel.failedRequest = {

        }
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.llGoogle -> checkIsAllowedToLoginAuth {
                loginViewModel.currentDeviceId = it
            }
            binding.llFacebook -> checkIsAllowedToLoginAuth {
                loginViewModel.currentDeviceId = it
                authLoginManager.facebookAuth()
            }
            binding.llDevice -> checkIsAllowedToLoginAuth {
                loginViewModel.currentDeviceId = it
                loginViewModel.login(AuthType.Device, it)
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