package com.milk.funcall.square.ui.frag

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.milk.funcall.common.ui.AbstractFragment
import com.milk.funcall.databinding.FragmentSquareBinding
import com.milk.funcall.square.ui.vm.SquareViewModel
import com.milk.simple.ktx.collectLatest
import com.milk.simple.ktx.statusBarPadding

class SquareFragment : AbstractFragment() {
    private val binding by lazy { FragmentSquareBinding.inflate(layoutInflater) }
    private val squareViewModel by viewModels<SquareViewModel>()

    override fun getRootView() = binding.root

    override fun initializeView() {
        binding.headerToolbar.statusBarPadding()
    }

    override fun initializeData() {
        squareViewModel.launchTimedRefresh()
        squareViewModel.getSquareInfo()
    }

    override fun initializeObserver() {
        squareViewModel.squareInfoFlow.collectLatest(this) { squareModel ->
            squareModel.userAvatarList?.let { binding.squareLayout.setUserAvatars(it) }
        }
        squareViewModel.onlineNumberFlow.collectLatest(this) {
            binding.tvOnlineNumber.text = it.toString()
        }
    }

    companion object {
        internal fun create(): Fragment {
            return SquareFragment()
        }
    }
}