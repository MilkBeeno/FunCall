package com.milk.funcall.main.ui.frag

import android.view.View
import androidx.fragment.app.viewModels
import com.milk.funcall.common.ui.AbstractFragment
import com.milk.funcall.databinding.FragmentHomeBinding
import com.milk.funcall.main.ui.vm.HomeViewModel

class HomeFragment : AbstractFragment() {
    private val homeViewModel by viewModels<HomeViewModel>()
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }

    override fun getRootView(): View = binding.root

    override fun initializeView() {

    }

    companion object {
        fun create() = HomeFragment()
    }
}