package com.milk.funcall.account.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.milk.funcall.BuildConfig
import com.milk.funcall.R
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityAboutUsBinding
import com.milk.simple.ktx.immersiveStatusBar
import com.milk.simple.ktx.showToast
import com.milk.simple.ktx.statusBarPadding
import com.milk.simple.ktx.viewBinding

class AboutUsActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityAboutUsBinding>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
    }

    private fun initializeView() {
        immersiveStatusBar()
        binding.headerToolbar.statusBarPadding()
        binding.headerToolbar.showArrowBack()
        binding.headerToolbar.setTitle(R.string.mine_about_us)
        binding.tvVersion.text = "v".plus(BuildConfig.VERSION_NAME)
        binding.llUserAgreement.setOnClickListener(this)
        binding.llUserPrivacy.setOnClickListener(this)
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.llUserAgreement -> {
                //todo hlc 2022.7.5 用户协议
                showToast("点击用户协议")
            }
            binding.llUserPrivacy -> {
                //todo hlc 2022.7.5 用户隐私
                showToast("点击用户隐私")
            }
        }
    }

    companion object {
        fun create(context: Context) {
            context.startActivity(Intent(context, AboutUsActivity::class.java))
        }
    }
}