package com.milk.funcall.ui.frag

import android.view.View
import androidx.fragment.app.viewModels
import com.milk.funcall.databinding.FragmentHomeBinding
import com.milk.funcall.ui.vm.HomeViewModel

class HomeFragment : AbstractFragment() {
    private val homeViewModel by viewModels<HomeViewModel>()
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }

    override fun getRootView(): View = binding.root

    override fun initializeView() {
        binding.tvHome.setOnClickListener {
            homeViewModel.addMessageData()
        }
    }

    companion object {
        fun create() = HomeFragment()
    }
}