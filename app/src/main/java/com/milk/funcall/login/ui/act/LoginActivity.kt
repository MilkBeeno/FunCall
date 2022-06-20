package com.milk.funcall.login.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.milk.funcall.R
import com.milk.funcall.common.author.AuthLoginManager
import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.common.enum.Gender
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityLoginBinding
import com.milk.funcall.login.ui.vm.LoginViewModel
import com.milk.simple.ktx.*
import com.milk.simple.mdr.KvManger

class LoginActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityLoginBinding>()
    private val loginViewModel by viewModels<LoginViewModel>()
    private val authLoginManager = AuthLoginManager(this)

    // 账号注册时可能走选择性别的逻辑、此时应当保存信息、应用内登录可在KV中获取性别信息
    private val gender by lazy { intent.getSerializableExtra(GENDER) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        immersiveStatusBar()
        setContentView(binding.root)
        initializeView()
    }

    private fun initializeView() {
        binding.llGoogle.setOnClickListener(this)
        binding.llFacebook.setOnClickListener(this)
        binding.llTourist.setOnClickListener(this)
        binding.ivPrivacyCheck.setOnClickListener(this)

        binding.tvPrivacy.text = string(R.string.login_privacy_desc)
        binding.tvPrivacy.setSpannableClick(
            Pair(
                string(R.string.login_user_agreement),
                colorClickableSpan(color(R.color.FF8E58FB)) {
                    showToast("点击 User Agreement")
                }), Pair(
                string(R.string.login_user_privacy),
                colorClickableSpan(color(R.color.FF8E58FB)) {
                    showToast("点击 User Privacy")
                })
        )
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.llGoogle -> checkPrivacyIsChecked {

            }
            binding.llFacebook -> checkPrivacyIsChecked {
                authLoginManager.facebookAuth()
            }
            binding.llTourist -> checkPrivacyIsChecked {

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

    private fun checkPrivacyIsChecked(request: () -> Unit) {
        if (loginViewModel.agreementPrivacy)
            request()
        else
            showToast(string(R.string.login_check_privacy))
    }

    override fun onInterceptKeyDownEvent(): Boolean {
        val gender = KvManger.getInt(KvKey.USER_GENDER)
        return gender == Gender.Man.value || gender == Gender.Woman.value
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        authLoginManager.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val GENDER = "GENDER"
        fun create(context: Context, gender: Gender? = null) =
            context.startActivity(Intent(context, LoginActivity::class.java)
                .apply { putExtra(GENDER, gender) })
    }
}