package com.milk.funcall.account.ui.frag

import android.view.View
import androidx.lifecycle.lifecycleScope
import com.milk.funcall.R
import com.milk.funcall.account.Account
import com.milk.funcall.account.ui.act.*
import com.milk.funcall.account.ui.dialog.LogoutDialog
import com.milk.funcall.common.ui.AbstractFragment
import com.milk.funcall.databinding.FragmentMineBinding
import com.milk.funcall.login.ui.act.GenderActivity
import com.milk.funcall.login.ui.act.LoginActivity
import com.milk.simple.ktx.gone
import com.milk.simple.ktx.visible
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MineFragment : AbstractFragment() {
    private val binding by lazy { FragmentMineBinding.inflate(layoutInflater) }
    private val dialog by lazy {
        LogoutDialog(requireActivity()) {
            Account.logout()
            GenderActivity.create(requireActivity())
        }
    }

    override fun getRootView(): View = binding.root

    override fun initializeView() {
        binding.llFollows.setOnClickListener(this)
        binding.llFans.setOnClickListener(this)
        binding.editProfile.setOnClickListener(this)
        binding.blackedList.setOnClickListener(this)
        binding.aboutUs.setOnClickListener(this)
        binding.signOut.setOnClickListener(this)
        binding.tvLogin.setOnClickListener(this)
        binding.flNotSigned.setOnClickListener(this)
        binding.editProfile.setOption(R.drawable.mine_edit_profile, R.string.mine_edit_profile)
        binding.blackedList.setOption(R.drawable.mine_blacked_list, R.string.mine_blacked_list)
        binding.aboutUs.setOption(R.drawable.mine_about_us, R.string.mine_about_us)
        binding.signOut.setOption(R.drawable.mine_sign_out, R.string.mine_sign_out, false)
    }

    override fun initializeObserver() {
        lifecycleScope.launch {
            Account.isLoggedState.collectLatest {
                if (it) binding.flNotSigned.gone() else binding.flNotSigned.visible()
            }
        }
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.llFollows -> FollowsActivity.create(requireContext())
            binding.llFans -> FansActivity.create(requireContext())
            binding.editProfile -> EditProfileActivity.create(requireContext())
            binding.blackedList -> BlackedListActivity.create(requireContext())
            binding.aboutUs -> AboutUsActivity.create(requireContext())
            binding.signOut -> dialog.show()
            binding.tvLogin -> LoginActivity.create(requireContext())
        }
    }

    companion object {
        fun create() = MineFragment()
    }
}