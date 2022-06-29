package com.milk.funcall.main.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityBlackedListBinding
import com.milk.simple.ktx.viewBinding

class BlackedListActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityBlackedListBinding>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    companion object {
        fun create(context: Context) {
            context.startActivity(Intent(context, BlackedListActivity::class.java))
        }
    }
}