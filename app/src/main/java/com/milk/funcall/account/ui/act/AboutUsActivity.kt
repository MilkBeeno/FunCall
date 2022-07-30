package com.milk.funcall.account.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.milk.funcall.BuildConfig
import com.milk.funcall.R
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.common.web.WebActivity
import com.milk.funcall.common.web.WebType
import com.milk.funcall.databinding.ActivityAboutUsBinding
import com.milk.simple.ktx.immersiveStatusBar
import com.milk.simple.ktx.navigationBarPadding
import com.milk.simple.ktx.statusBarPadding

class AboutUsActivity : AbstractActivity() {
    private val binding by lazy { ActivityAboutUsBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
    }

    private fun initializeView() {
        immersiveStatusBar()
        binding.headerToolbar.statusBarPadding()
        binding.root.navigationBarPadding()
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
                WebActivity.create(
                    this,
                    WebType.UserAgreement.value,
                    "http://funcallnow.com/terms.html"
                )
            }
            binding.llUserPrivacy -> {
                WebActivity.create(
                    this,
                    WebType.PrivacyService.value,
                    "http://funcallnow.com/privacy.html"
                )
            }
        }
    }

    companion object {
        fun create(context: Context) {
            context.startActivity(Intent(context, AboutUsActivity::class.java))
        }
    }
}