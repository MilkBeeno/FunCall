package com.milk.funcall.login.ui.act

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.NoCopySpan
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import com.milk.funcall.R
import com.milk.funcall.app.ui.act.MainActivity
import com.milk.funcall.common.author.AuthType
import com.milk.funcall.common.author.DeviceManager
import com.milk.funcall.common.author.LoginManager
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.net.error.ApiErrorCode
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.common.web.WebActivity
import com.milk.funcall.common.web.WebType
import com.milk.funcall.databinding.ActivityLoginBinding
import com.milk.funcall.login.ui.dialog.LoadingDialog
import com.milk.funcall.login.ui.dialog.MaxClientDialog
import com.milk.funcall.login.ui.vm.LoginViewModel
import com.milk.simple.ktx.*

class LoginActivity : AbstractActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val loginViewModel by viewModels<LoginViewModel>()
    private val authLoginManager by lazy { LoginManager(this) }
    private val loadingDialog by lazy { LoadingDialog(this) }
    private val maxClientDialog by lazy { MaxClientDialog(this) }
    private var isNotAuthorizing: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        initializeCallback()
    }

    private fun initializeView() {
        FireBaseManager.logEvent(FirebaseKey.OPEN_LOGINS_PAGE)
        immersiveStatusBar()
        binding.root.navigationBarPadding()
        binding.llGoogle.setOnClickListener(this)
        binding.llFacebook.setOnClickListener(this)
        binding.llDevice.setOnClickListener(this)
        binding.ivPrivacyCheck.setOnClickListener(this)
        binding.tvPrivacy.text = string(R.string.login_privacy_desc)
        // 在内部进行处理避免混淆问题
        binding.tvPrivacy.setSpannableClick(
            Pair(
                string(R.string.login_user_agreement),
                colorClickableSpan(color(R.color.FF8E58FB)) {
                    FireBaseManager
                        .logEvent(FirebaseKey.CLICK_LOGIN_PAGE_USER_AGREEMENT)
                    WebActivity.create(
                        this,
                        WebType.UserAgreement.value,
                        "https://justfuncall.com/terms.html"
                    )
                }),
            Pair(
                string(R.string.login_user_privacy),
                colorClickableSpan(color(R.color.FF8E58FB)) {
                    FireBaseManager
                        .logEvent(FirebaseKey.CLICK_LOGIN_PAGE_PRIVACY_POLICY)
                    WebActivity.create(
                        this,
                        WebType.PrivacyService.value,
                        "https://justfuncall.com/privacy.html"
                    )
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
            PresetProfileActivity.create(this)
        }
        loginViewModel.failedRequest = {
            isNotAuthorizing = true
            loadingDialog.dismiss()
            if (it == ApiErrorCode.MAX_CLIENT_NUMBER) {
                FireBaseManager
                    .logEvent(FirebaseKey.MAX_REGISTRATIONS_REACHED_SHOW)
                mainScope { maxClientDialog.show() }
            }
        }
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.llGoogle -> checkIsAllowedToLoginAuth {
                FireBaseManager.logEvent(FirebaseKey.LOGINS_WITH_GOOGLE)
                if (isNotAuthorizing) {
                    isNotAuthorizing = false
                    loginViewModel.currentDeviceId = it
                    authLoginManager.googleAuth()
                }
            }
            binding.llFacebook -> checkIsAllowedToLoginAuth {
                FireBaseManager.logEvent(FirebaseKey.LOGINS_WITH_FB)
                if (isNotAuthorizing) {
                    isNotAuthorizing = false
                    authLoginManager.facebookAuth()
                    loginViewModel.currentDeviceId = it
                }
            }
            binding.llDevice -> checkIsAllowedToLoginAuth {
                FireBaseManager.logEvent(FirebaseKey.LOGINS_WITH_GUEST)
                if (isNotAuthorizing) {
                    isNotAuthorizing = false
                    loginViewModel.currentDeviceId = it
                    authLoginManager.success?.invoke(AuthType.Device, it)
                }
            }
            binding.ivPrivacyCheck -> {
                loginViewModel.agreementPrivacy = !loginViewModel.agreementPrivacy
                binding.ivPrivacyCheck.setImageResource(
                    if (loginViewModel.agreementPrivacy) {
                        R.drawable.login_privacy_checked
                    } else {
                        R.drawable.login_privacy_no_check
                    }
                )
            }
        }
    }

    private fun checkIsAllowedToLoginAuth(request: (String) -> Unit) {
        if (loginViewModel.agreementPrivacy) {
            val deviceId = DeviceManager.number
            if (deviceId.isNotBlank()) {
                mainScope { request(deviceId) }
            } else {
                showToast(string(R.string.login_obtain_device_failed))
            }
        } else {
            showToast(string(R.string.login_check_privacy))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        authLoginManager.onActivityResult(requestCode, resultCode, data)
    }

    /** 对指定文字变色并赋予文字点击事件 */
    private fun TextView.setSpannableClick(vararg targets: Pair<String, ClickableSpan>) {
        try {
            if (text.toString().trim().isBlank()) return
            val content = text.toString()
            val builder = SpannableStringBuilder(content)
            targets.forEach {
                if (content.contains(it.first)) {
                    val startIndex = content.indexOf(it.first, ignoreCase = true)
                    val endIndex = startIndex + it.first.length
                    val colorFlags = Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    if (startIndex > 0 && endIndex < content.length)
                        builder.setSpan(it.second, startIndex, endIndex, colorFlags)
                }
            }
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
            text = builder
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** 指定文字变色和点击抽象实现类 */
    abstract inner class NoCopyClickableSpan : ClickableSpan(), NoCopySpan {
        override fun onClick(p0: View) = Unit
    }

    /** 指定文字变色点击事件实例 */
    private fun colorClickableSpan(color: Int, clickRequest: () -> Unit) =
        object : NoCopyClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = color
                ds.isUnderlineText = false
                ds.clearShadowLayer()
            }

            override fun onClick(p0: View) = clickRequest()
        }

    companion object {
        internal fun create(context: Context) =
            context.startActivity(Intent(context, LoginActivity::class.java))
    }
}