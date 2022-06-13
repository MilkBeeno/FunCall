package com.milk.funcall.ui.frag

import android.view.View
import com.milk.funcall.R
import com.milk.funcall.databinding.FragmentMessageBinding
import com.milk.funcall.ui.act.MessageActivity

class MessageFragment : AbstractFragment() {
    private val binding by lazy { FragmentMessageBinding.inflate(layoutInflater) }

    override fun getRootView(): View = binding.root

    override fun initializeData() {

    }

    override fun initializeView() {
        binding.headerToolbar.setTitle(R.string.message_title)
        binding.tvName.setOnClickListener {
            MessageActivity.create(requireContext(), 1234, "safsdf")
        }
    }

    companion object {
        fun create() = MessageFragment()
    }
}