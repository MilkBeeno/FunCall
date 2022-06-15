package com.milk.funcall.login.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityLoginBinding
import com.milk.simple.ktx.viewBinding

class LoginActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityLoginBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    companion object {
        fun create(context: Context) =
            context.startActivity(Intent(context, LoginActivity::class.java))
    }
}