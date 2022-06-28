package com.milk.funcall.common.web

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityWebBinding

class WebActivity : AbstractActivity() {
    private val binding by lazy { ActivityWebBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    companion object {
        fun create(context: Context) {
            context.startActivity(Intent(context, WebActivity::class.java))
        }
    }
}