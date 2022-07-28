package com.milk.funcall.account.ui.frag

import android.view.View
import com.milk.funcall.R
import com.milk.funcall.account.Account
import com.milk.funcall.account.ui.act.*
import com.milk.funcall.account.ui.dialog.LogoutDialog
import com.milk.funcall.common.media.loader.ImageLoader
import com.milk.funcall.common.ui.AbstractFragment
import com.milk.funcall.databinding.FragmentMineBinding
import com.milk.funcall.login.ui.act.GenderActivity
import com.milk.funcall.login.ui.act.LoginActivity
import com.milk.funcall.user.ui.config.AvatarImage
import com.milk.simple.ktx.collectLatest
import com.milk.simple.ktx.gone
import com.milk.simple.ktx.string
import com.milk.simple.ktx.visible

class MineFragment : AbstractFragment() {
    private val binding by lazy { FragmentMineBinding.inflate(layoutInflater) }
    private val defaultAvatar by lazy { AvatarImage().obtain(Account.userGender) }
    private val logoutDialog by lazy { LogoutDialog(requireActivity()) }

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
        logoutDialog.setOnConfirmListener {
            GenderActivity.create(requireActivity())
            Account.logout()
        }
    }

    override fun initializeObserver() {
        Account.userLoggedFlow.collectLatest(this) {
            if (it) binding.flNotSigned.gone() else binding.flNotSigned.visible()
        }
        Account.userAvatarFlow.collectLatest(this) {
            if (it.isNotBlank()) {
                ImageLoader.Builder()
                    .loadAvatar(it)
                    .target(binding.ivUserAvatar)
                    .build()
            } else binding.ivUserAvatar.setImageResource(defaultAvatar)
        }
        Account.userGenderFlow.collectLatest(this) {
            binding.ivUserGender.updateGender(it)
        }
        Account.userNameFlow.collectLatest(this) {
            binding.tvUserName.text =
                it.ifBlank { requireActivity().string(R.string.mine_default_user_name) }
        }
        Account.userFansFlow.collectLatest(this) {
            binding.tvFans.text = it.toString()
        }
        Account.userFollowsFlow.collectLatest(this) {
            binding.tvFollows.text = it.toString()
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
            binding.signOut -> logoutDialog.show()
            binding.tvLogin -> LoginActivity.create(requireContext())
        }
    }

    companion object {
        fun create() = MineFragment()
    }
}