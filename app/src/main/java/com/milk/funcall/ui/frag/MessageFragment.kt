package com.milk.funcall.ui.frag

import android.view.View
import com.milk.funcall.databinding.FragmentMessageBinding

class MessageFragment : AbstractFragment() {
    private val binding by lazy { FragmentMessageBinding.inflate(layoutInflater) }

    override fun getContentView(): View = binding.root

    override fun initializeView() {

    }

    companion object {
        fun create() = MessageFragment()
    }
}