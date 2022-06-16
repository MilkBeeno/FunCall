package com.milk.funcall.login.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.milk.funcall.R
import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.common.emun.Gender
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityLoginBinding
import com.milk.funcall.login.ui.vm.LoginViewModel
import com.milk.simple.ktx.*
import com.milk.simple.mdr.KvManger

class LoginActivity : AbstractActivity() {
    private val gender by lazy { intent.getSerializableExtra(GENDER) }
    val binding by viewBinding<ActivityLoginBinding>()
    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        immersiveStatusBar()
        setContentView(binding.root)
        initializePrivacy()
    }

    private fun initializePrivacy() {
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

    override fun onInterceptKeyDownEvent(): Boolean {
        val gender = KvManger.getInt(KvKey.USER_GENDER)
        return gender == Gender.Man.value || gender == Gender.Woman.value
    }

    companion object {
        private const val GENDER = "GENDER"
        fun create(context: Context, gender: Gender? = null) =
            context.startActivity(Intent(context, LoginActivity::class.java)
                .apply { putExtra(GENDER, gender) })
    }
}