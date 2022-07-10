package com.milk.funcall.user.ui.act

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.asLiveData
import com.milk.funcall.R
import com.milk.funcall.common.media.ImageLoader
import com.milk.funcall.common.paging.SimpleGridDecoration
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.common.ui.manager.NoScrollGridLayoutManager
import com.milk.funcall.databinding.ActivityUserInfoBinding
import com.milk.funcall.user.data.UserMediaModel
import com.milk.funcall.user.type.Gender
import com.milk.funcall.user.ui.adapter.UserImageAdapter
import com.milk.funcall.user.ui.vm.UserTotalInfoViewModel
import com.milk.simple.ktx.*

class UserTotalInfoActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityUserInfoBinding>()
    private val userTotalInfoViewModel by viewModels<UserTotalInfoViewModel>()
    private val userId by lazy { intent.getLongExtra(USER_ID, 0) }
    private var isBlacked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        initializeObserver()
        loadUserInfo()
    }

    private fun initializeView() {
        immersiveStatusBar()
        binding.headerToolbar.statusBarPadding()
        binding.root.navigationBarPadding()
        binding.headerToolbar.showArrowBack()
        binding.link.tvCopy.setOnClickListener(this)
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
            setVideoList(it.userVideoList)
            setImageList(it.userImageList)
        }
        userTotalInfoViewModel.userFollowedChangeFlow.asLiveData().observe(this) {
            setFollowState(it)
        }
    }

    private fun setUserAvatar(avatar: String, gender: String) {
        val isMale = gender == Gender.Man.value
        ImageLoader.Builder()
            .loadAvatar(avatar, isMale)
            .target(binding.top.ivUserAvatar)
            .build()
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

    private fun setVideoList(imageList: MutableList<UserMediaModel>) {
        if (imageList.isNotEmpty()) {
            binding.tvVideo.visible()
            binding.flVideo.visible()
        }
    }

    private fun setImageList(imageList: MutableList<UserMediaModel>) {
        if (imageList.isNotEmpty()) {
            binding.tvImage.visible()
            binding.rvImage.visible()
            binding.rvImage.layoutManager = NoScrollGridLayoutManager(this, 2)
            binding.rvImage.addItemDecoration(SimpleGridDecoration(this))
            binding.rvImage.adapter = UserImageAdapter(imageList)
        }
    }

    private fun loadUserInfo() {
        userTotalInfoViewModel.getUserTotalInfo(userId)
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.link.tvCopy -> {
                val label = string(R.string.app_name)
                val link = binding.link.tvContact.text.toString()
                val cmb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cmb.setPrimaryClip(ClipData.newPlainText(label, link))
                showToast(string(R.string.user_info_copy_success))
            }
        }
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