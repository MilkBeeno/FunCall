package com.milk.funcall.main.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityFansBinding
import com.milk.simple.ktx.viewBinding

class FansActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityFansBinding>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    companion object {
        fun create(context: Context) {
            context.startActivity(Intent(context, FansActivity::class.java))
        }
    }
}