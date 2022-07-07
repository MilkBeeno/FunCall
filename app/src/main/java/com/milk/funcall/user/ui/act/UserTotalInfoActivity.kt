package com.milk.funcall.user.ui.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.asLiveData
import com.milk.funcall.R
import com.milk.funcall.common.imageLoad.loadAvatar
import com.milk.funcall.databinding.ActivityUserInfoBinding
import com.milk.funcall.user.type.Gender
import com.milk.funcall.user.ui.vm.UserTotalInfoViewModel
import com.milk.simple.ktx.*

class UserTotalInfoActivity : AppCompatActivity() {
    private val binding by viewBinding<ActivityUserInfoBinding>()
    private val userTotalInfoViewModel by viewModels<UserTotalInfoViewModel>()
    private val userId by lazy { intent.getLongExtra(USER_ID, 0) }
    private var isBlacked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        initializeObserver()
        loadData()
    }

    private fun initializeView() {
        immersiveStatusBar(binding.headerToolbar)
        binding.headerToolbar.showArrowBack()
        binding.userLoading.visible()
        binding.nlContent.gone()
        binding.llUserNext.gone()
        binding.top.llFollow.gone()
        binding.link.clLink.gone()
    }

    private fun initializeObserver() {
        userTotalInfoViewModel.dataRequestStateFlow.asLiveData().observe(this) {
            if (it) {
                binding.userLoading.gone()
                binding.nlContent.visible()
                binding.llUserNext.visible()
                binding.userLoading.cancelAnimation()
            } else {
                showToast(string(R.string.user_info_obtain_failed))
                finish()
            }
        }
        userTotalInfoViewModel.userTotalInfoFlow.asLiveData().observe(this) {
            isBlacked = it.isBlacked
            setUserAvatar(it.userAvatar, it.userGender)
            setFollowState(it.isFollowed)
            setUserBasic(it.userIdx, it.userName, it.userBio)
            setLink(it.userLink)
        }
        userTotalInfoViewModel.userFollowedChangeFlow.asLiveData().observe(this) {
            setFollowState(it)
        }
    }

    private fun setUserAvatar(avatar: String, gender: String) {
        val default = if (gender == Gender.Woman.value)
            R.drawable.common_default_woman
        else
            R.drawable.common_default_man
        binding.top.ivUserAvatar.loadAvatar(avatar, default)
        binding.top.ivUserGender.updateGender(gender)
    }

    private fun setFollowState(isFollowed: Boolean) {
        binding.top.llFollow.visible()
        val params = binding.top.ivFollow.layoutParams as LinearLayoutCompat.LayoutParams
        if (isFollowed) {
            binding.top.tvFollow.gone()
            binding.top.ivFollow.setImageResource(R.drawable.user_info_followed)
            binding.top.llFollow.setBackgroundResource(R.drawable.shape_user_info_followed)
            params.marginStart = dp2px(8f)
            params.marginEnd = dp2px(8f)
            binding.top.ivFollow.layoutParams = params
        } else {
            binding.top.tvFollow.visible()
            binding.top.ivFollow.setImageResource(R.drawable.user_info_un_follow)
            binding.top.llFollow.setBackgroundResource(R.drawable.shape_user_info_un_follow)
            params.marginStart = dp2px(10f)
            params.marginEnd = 0
            binding.top.ivFollow.layoutParams = params
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUserBasic(userId: String, userName: String, userBio: String) {
        binding.tvUserId.text = "ID : ".plus(userId)
        binding.tvUserName.text = userName
        binding.tvUserBio.text = userBio
    }

    private fun setLink(link: String) {
        if (link.isNotBlank()) {
            binding.link.clLink.visible()
            binding.link.tvNotLink.gone()
            binding.link.tvContact.text = link
        } else {
            binding.link.clLink.gone()
            binding.link.tvNotLink.visible()
        }
    }

    private fun loadData() {
        userTotalInfoViewModel.getUserTotalInfo(userId)
    }

    private fun Context.dp2px(float: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            float,
            resources.displayMetrics
        ).toInt()
    }

    companion object {
        private const val USER_ID = "USER_ID"
        fun create(context: Context, userId: Long = 0) {
            val intent = Intent(context, UserTotalInfoActivity::class.java)
            intent.putExtra(USER_ID, userId)
            context.startActivity(intent)
        }
    }
}