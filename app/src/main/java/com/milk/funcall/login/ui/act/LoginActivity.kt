package com.milk.funcall.login.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.milk.funcall.R
import com.milk.funcall.common.author.AuthLoginManager
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityLoginBinding
import com.milk.funcall.login.ui.vm.LoginViewModel
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
                CreateNameActivity.create(this)
            }
            binding.llFacebook -> checkPrivacyIsChecked {
                authLoginManager.facebookAuth()
                CreateNameActivity.create(this)
            }
            binding.llTourist -> checkPrivacyIsChecked {
                CreateNameActivity.create(this)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        authLoginManager.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        fun create(context: Context) =
            context.startActivity(Intent(context, LoginActivity::class.java))
    }
}