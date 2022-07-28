package com.milk.funcall.common.web

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.milk.funcall.R
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityWebBinding
import com.milk.simple.ktx.string

class WebActivity : AbstractActivity() {
    private val binding by lazy { ActivityWebBinding.inflate(layoutInflater) }
    private val type by lazy { intent.getStringExtra(WEB_VIEW_TYPE).toString() }
    private val url by lazy { intent.getStringExtra(WEB_VIEW_URL).toString() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.headerToolbar.showArrowBack()
        binding.headerToolbar.setTitle(
            when (type) {
                WebType.UserAgreement.value ->
                    string(R.string.login_user_agreement)
                WebType.PrivacyService.value ->
                    string(R.string.login_user_privacy)
                else -> ""
            }
        )
        binding.webView.loadUrl(url)
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url.toString())
                return true
            }
        }
    }

    companion object {
        private const val WEB_VIEW_TYPE = "WEB_VIEW_TYPE"
        private const val WEB_VIEW_URL = "WEB_VIEW_URL"
        fun create(context: Context, type: String, url: String) {
            val intent = Intent(context, WebActivity::class.java)
            intent.putExtra(WEB_VIEW_TYPE, type)
            intent.putExtra(WEB_VIEW_URL, url)
            context.startActivity(intent)
        }
    }
}