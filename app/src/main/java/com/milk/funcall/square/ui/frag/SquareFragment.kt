package com.milk.funcall.square.ui.frag

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.milk.funcall.R
import com.milk.funcall.account.Account
import com.milk.funcall.app.AppConfig
import com.milk.funcall.common.ui.AbstractFragment
import com.milk.funcall.databinding.FragmentSquareBinding
import com.milk.funcall.square.ui.dialog.SquareRulesDialog
import com.milk.funcall.square.ui.vm.SquareViewModel
import com.milk.simple.ktx.collectLatest
import com.milk.simple.ktx.statusBarPadding
import com.milk.simple.ktx.string

class SquareFragment : AbstractFragment() {
    private val binding by lazy { FragmentSquareBinding.inflate(layoutInflater) }
    private val squareViewModel by viewModels<SquareViewModel>()
    private val squareRulesDialog by lazy { SquareRulesDialog(requireActivity()) }

    override fun getRootView() = binding.root

    override fun initializeView() {
        binding.headerToolbar.statusBarPadding()
        binding.ivRules.setOnClickListener(this)
    }

    override fun initializeData() {
        squareViewModel.launchTimedRefresh()
        updateMatchTimes()
    }

    private fun updateMatchTimes() {
        binding.tvStart.text = if (Account.userSubscribe) {
            requireActivity().string(R.string.square_start)
        } else {
            var usedTimes: Int = 0
            var totalTime: Int = 0
            if (AppConfig.squareFreeMatchTimes <= AppConfig.freeMatchTimes) {
                usedTimes = AppConfig.squareFreeMatchTimes
                totalTime = AppConfig.freeMatchTimes
            } else {
                usedTimes = AppConfig.squareMatchTimes
                totalTime = AppConfig.matchAdRewardTime
            }
            requireActivity().string(R.string.square_start).plus(" ").plus("(")
                .plus(usedTimes).plus("/").plus(totalTime).plus(")")
        }
    }

    override fun initializeObserver() {
        Account.userGenderFlow.collectLatest(this) {
            squareViewModel.getSquareInfo()
        }
        squareViewModel.squareInfoFlow.collectLatest(this) { squareModel ->
            squareModel.userAvatarList?.let { binding.squareLayout.setUserAvatars(it) }
        }
        squareViewModel.onlineNumberFlow.collectLatest(this) {
            binding.tvOnlineNumber.text = it.toString()
        }
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.ivRules -> {
                squareRulesDialog.show()
            }
        }
    }

    companion object {
        internal fun create(): Fragment {
            return SquareFragment()
        }
    }
}