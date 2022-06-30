package com.milk.funcall.account.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityAboutUsBinding
import com.milk.simple.ktx.viewBinding

class AboutUsActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityAboutUsBinding>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    companion object {
        fun create(context: Context) {
            context.startActivity(Intent(context, AboutUsActivity::class.java))
        }
    }
}