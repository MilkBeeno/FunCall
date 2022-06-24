package com.milk.funcall.main.ui.frag

import android.view.View
import com.milk.funcall.R
import com.milk.funcall.common.ui.AbstractFragment
import com.milk.funcall.databinding.FragmentMineBinding
import com.milk.funcall.main.Account
import com.milk.funcall.main.ui.act.*

class MineFragment : AbstractFragment() {
    private val binding by lazy { FragmentMineBinding.inflate(layoutInflater) }

    override fun getRootView(): View = binding.root

    override fun initializeView() {
        binding.llFollows.setOnClickListener(this)
        binding.llFans.setOnClickListener(this)
        binding.editProfile.setOnClickListener(this)
        binding.blackedList.setOnClickListener(this)
        binding.aboutUs.setOnClickListener(this)
        binding.signOut.setOnClickListener(this)
        binding.editProfile.setOption(R.drawable.mine_edit_profile, R.string.mine_edit_profile)
        binding.blackedList.setOption(R.drawable.mine_blacked_list, R.string.mine_blacked_list)
        binding.aboutUs.setOption(R.drawable.mine_about_us, R.string.mine_about_us)
        binding.signOut.setOption(R.drawable.mine_sign_out, R.string.mine_sign_out, false)
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.llFollows -> {
                FollowsActivity.create(requireContext())
            }
            binding.llFans -> {
                FansActivity.create(requireContext())
            }
            binding.editProfile -> {
                EditProfileActivity.create(requireContext())
            }
            binding.blackedList -> {
                BlackedListActivity.create(requireContext())
            }
            binding.aboutUs -> {
                AboutUsActivity.create(requireContext())
            }
            binding.signOut -> {
                Account.logout()
            }
        }
    }

    companion object {
        fun create() = MineFragment()
    }
}