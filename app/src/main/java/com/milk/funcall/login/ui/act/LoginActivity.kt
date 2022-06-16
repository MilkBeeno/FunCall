package com.milk.funcall.login.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.common.emun.Gender
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityLoginBinding
import com.milk.funcall.login.ui.vm.LoginViewModel
import com.milk.simple.ktx.immersiveStatusBar
import com.milk.simple.ktx.viewBinding
import com.milk.simple.mdr.KvManger

class LoginActivity : AbstractActivity() {
    private val gender by lazy { intent.getSerializableExtra(GENDER) }
    val binding by viewBinding<ActivityLoginBinding>()
    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        immersiveStatusBar()
        setContentView(binding.root)
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