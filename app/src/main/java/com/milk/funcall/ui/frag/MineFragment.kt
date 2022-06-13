package com.milk.funcall.ui.frag

import android.view.View
import com.milk.funcall.databinding.FragmentMineBinding

class MineFragment : AbstractFragment() {
    private val binding by lazy { FragmentMineBinding.inflate(layoutInflater) }

    override fun getRootView(): View = binding.root

    override fun initializeView() {

    }

    companion object {
        fun create() = MineFragment()
    }
}