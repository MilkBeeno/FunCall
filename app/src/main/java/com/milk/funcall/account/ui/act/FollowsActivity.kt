package com.milk.funcall.account.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityFollowsBinding
import com.milk.simple.ktx.viewBinding

class FollowsActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityFollowsBinding>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    companion object {
        fun create(context: Context) {
            context.startActivity(Intent(context, FollowsActivity::class.java))
        }
    }
}