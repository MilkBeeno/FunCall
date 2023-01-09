package com.milk.funcall.square.ui.frag

import androidx.fragment.app.Fragment
import com.milk.funcall.common.ui.AbstractFragment
import com.milk.funcall.databinding.FragmentSquareBinding
import com.milk.simple.ktx.statusBarPadding

class SquareFragment : AbstractFragment() {
    private val binding by lazy { FragmentSquareBinding.inflate(layoutInflater) }

    override fun getRootView() = binding.root

    override fun initializeView() {
        binding.headerToolbar.statusBarPadding()
    }

    companion object {
        internal fun create(): Fragment {
            return SquareFragment()
        }
    }
}