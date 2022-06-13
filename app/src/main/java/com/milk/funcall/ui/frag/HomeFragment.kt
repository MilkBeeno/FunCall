package com.milk.funcall.ui.frag

import android.view.View
import com.milk.funcall.databinding.FragmentHomeBinding

class HomeFragment : AbstractFragment() {
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }

    override fun getContentView(): View = binding.root

    override fun initializeView() {

    }

    companion object {
        fun create() = HomeFragment()
    }
}