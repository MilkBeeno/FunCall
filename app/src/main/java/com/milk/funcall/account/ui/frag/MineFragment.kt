package com.milk.funcall.account.ui.frag

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.milk.funcall.R
import com.milk.funcall.account.Account
import com.milk.funcall.account.ui.act.*
import com.milk.funcall.account.ui.dialog.LogoutDialog
import com.milk.funcall.app.ui.MainViewModel
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.media.loader.ImageLoader
import com.milk.funcall.common.ui.AbstractFragment
import com.milk.funcall.databinding.FragmentMineBinding
import com.milk.funcall.login.ui.act.GenderActivity
import com.milk.funcall.login.ui.act.LoginActivity
import com.milk.funcall.login.ui.dialog.LoadingDialog
import com.milk.funcall.user.ui.config.AvatarImage
import com.milk.simple.ktx.*

class MineFragment : AbstractFragment() {
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val binding by lazy { FragmentMineBinding.inflate(layoutInflater) }
    private val defaultAvatar by lazy { AvatarImage().obtain(Account.userGender) }
    private val logoutDialog by lazy { LogoutDialog(requireActivity()) }
    private val loadingDialog by lazy { LoadingDialog(requireActivity()) }

    override fun getRootView(): View = binding.root

    override fun initializeView() {
        binding.llFollows.setOnClickListener(this)
        binding.llFans.setOnClickListener(this)
        binding.editProfile.setOnClickListener(this)
        binding.recharge.setOnClickListener(this)
        binding.blackedList.setOnClickListener(this)
        binding.aboutUs.setOnClickListener(this)
        binding.signOut.setOnClickListener(this)
        binding.tvLogin.setOnClickListener(this)
        binding.flNotSigned.setOnClickListener(this)
        binding.editProfile.setOption(R.drawable.mine_edit_profile, R.string.mine_edit_profile)
        binding.recharge.setOption(R.drawable.mine_recharge, R.string.mine_recharge, requireContext().color(R.color.FF744311))
        binding.blackedList.setOption(R.drawable.mine_blacked_list, R.string.mine_blacked_list)
        binding.aboutUs.setOption(R.drawable.mine_about_us, R.string.mine_about_us)
        binding.signOut.setOption(R.drawable.mine_sign_out, R.string.mine_sign_out, showLine = false)
        logoutDialog.setOnConfirmListener {
            FireBaseManager.logEvent(FirebaseKey.LOG_OUT_SUCCESS)
            loadingDialog.show()
            Account.logout()
            mainViewModel.uploadDeviceInfo()
        }
    }

    override fun initializeObserver() {
        Account.userLoggedFlow.collectLatest(this) {
            if (it) {
                binding.flNotSigned.gone()
            } else {
                binding.flNotSigned.visible()
            }
        }
        Account.userAvatarFlow.collectLatest(this) {
            if (it.isNotBlank()) {
                ImageLoader.Builder().loadAvatar(it).target(binding.ivUserAvatar).build()
            } else {
                binding.ivUserAvatar.setImageResource(defaultAvatar)
            }
        }
        Account.userGenderFlow.collectLatest(this) {
            binding.ivUserGender.updateGender(it)
        }
        Account.userNameFlow.collectLatest(this) {
            binding.tvUserName.text = it.ifBlank { requireActivity().string(R.string.mine_default_user_name) }
        }
        Account.userFansFlow.collectLatest(this) {
            binding.tvFans.text = it.toString()
        }
        Account.userFollowsFlow.collectLatest(this) {
            binding.tvFollows.text = it.toString()
        }
        mainViewModel.logOutFlow.collectLatest(this) {
            loadingDialog.dismiss()
            if (it) GenderActivity.create(requireActivity())
        }
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.llFollows -> {
                FireBaseManager.logEvent(FirebaseKey.CLICK_THE_FOLLOW)
                FollowsActivity.create(requireContext())
            }
            binding.llFans -> {
                FireBaseManager.logEvent(FirebaseKey.CLICK_THE_FAN)
                FansActivity.create(requireContext())
            }
            binding.editProfile -> {
                FireBaseManager.logEvent(FirebaseKey.CLICK_ON_EDIT_PROFILE)
                EditProfileActivity.create(requireContext())
            }
            binding.recharge -> {
                FireBaseManager.logEvent(FirebaseKey.CLICK_THE_SUBSCRIPTION_PORTAL)
                RechargeActivity.create(requireContext())
            }
            binding.blackedList -> {
                FireBaseManager.logEvent(FirebaseKey.CLICK_BLACKLIST)
                BlackedListActivity.create(requireContext())
            }
            binding.aboutUs -> {
                FireBaseManager.logEvent(FirebaseKey.CLICK_ABOUT_OUR)
                AboutUsActivity.create(requireContext())
            }
            binding.signOut -> {
                FireBaseManager.logEvent(FirebaseKey.CLICK_THE_LOG_OUT)
                logoutDialog.show()
            }
            binding.tvLogin -> {
                FireBaseManager.logEvent(FirebaseKey.CLICK_ON_MY_PAGE_LOGIN_PORTAL)
                LoginActivity.create(requireContext())
            }
        }
    }

    companion object {
        internal fun create(): Fragment {
            return MineFragment()
        }
    }
}