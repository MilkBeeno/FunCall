package com.milk.funcall.account.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.milk.funcall.R
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityBlackedListBinding
import com.milk.simple.ktx.immersiveStatusBar
import com.milk.simple.ktx.viewBinding

class BlackedListActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityBlackedListBinding>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
    }

    private fun initializeView() {
        immersiveStatusBar(binding.headerToolbar)
        binding.headerToolbar.showArrowBack()
        binding.headerToolbar.setTitle(R.string.mine_blacked_list)
    }

    companion object {
        fun create(context: Context) {
            context.startActivity(Intent(context, BlackedListActivity::class.java))
        }
    }
}