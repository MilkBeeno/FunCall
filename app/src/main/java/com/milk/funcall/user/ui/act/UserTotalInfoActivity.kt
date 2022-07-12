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
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.R
import com.milk.funcall.common.constrant.KvKey
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
        binding.ivUserNext.setOnClickListener(this)
    }

    private fun initializeObserver() {
        userTotalInfoViewModel.userTotalInfoFlow.asLiveData().observe(this) {
            if (it != null) {
                isBlacked = it.isBlacked
                binding.lvLoading.gone()
                binding.llUserNext.visible()
                binding.basic.root.visible()
                binding.link.root.visible()
                binding.llMedia.visible()
                setUserAvatar(it.userAvatar, it.userGender)
                setUserFollow(it.isFollowed)
                setUserBasic(it.userIdx, it.userName, it.userBio)
                setUserLink(it.userLink)
                setUserVideo(it.userVideoList)
                setUserImage(it.userImageList)
                setMediaEmpty(it.userVideoList.isEmpty() && it.userImageList.isEmpty())
            } else {
                showToast(string(R.string.user_info_obtain_failed))
                finish()
            }
        }
        userTotalInfoViewModel.userFollowedChangeFlow.asLiveData().observe(this) {
            setUserFollow(it)
        }
    }

    private fun setUserAvatar(avatar: String, gender: String) {
        ImageLoader.Builder()
            .loadAvatar(avatar, isMale = gender == Gender.Man.value)
            .target(binding.basic.ivUserAvatar)
            .build()
        binding.basic.ivUserGender.updateGender(gender)
    }

    private fun setUserFollow(isFollowed: Boolean) {
        binding.basic.llFollow.visible()
        val params = binding.basic.ivFollow.layoutParams as LinearLayoutCompat.LayoutParams
        if (isFollowed) {
            binding.basic.tvFollow.gone()
            binding.basic.ivFollow.setImageResource(R.drawable.user_info_followed)
            binding.basic.llFollow.setBackgroundResource(R.drawable.shape_user_info_followed)
            params.marginStart = dp2px(8f)
            params.marginEnd = dp2px(8f)
            binding.basic.ivFollow.layoutParams = params
        } else {
            binding.basic.tvFollow.visible()
            binding.basic.ivFollow.setImageResource(R.drawable.user_info_un_follow)
            binding.basic.llFollow.setBackgroundResource(R.drawable.shape_user_info_un_follow)
            params.marginStart = dp2px(10f)
            params.marginEnd = 0
            binding.basic.ivFollow.layoutParams = params
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUserBasic(userId: String, userName: String, userBio: String) {
        binding.tvUserId.text = "ID : ".plus(userId)
        binding.tvUserName.text = userName
        binding.tvUserBio.text = userBio
    }

    private fun setUserLink(link: String) {
        if (link.isNotBlank()) {
            binding.link.clLink.visible()
            binding.link.tvNotLink.gone()
            binding.link.tvContact.text = link
        } else {
            binding.link.clLink.gone()
            binding.link.tvNotLink.visible()
        }
    }

    private fun setUserVideo(imageList: MutableList<UserMediaModel>) {
        if (imageList.isNotEmpty()) {
            binding.tvVideo.visible()
            binding.flVideo.visible()
        }
    }

    private fun setUserImage(imageList: MutableList<UserMediaModel>) {
        if (imageList.isNotEmpty()) {
            binding.tvImage.visible()
            binding.rvImage.visible()
            binding.rvImage.layoutManager = NoScrollGridLayoutManager(this, 2)
            binding.rvImage.addItemDecoration(SimpleGridDecoration(this))
            binding.rvImage.adapter = UserImageAdapter(imageList) {
                ImageMediaActivity.create(this, it)
                LiveEventBus.get<MutableList<UserMediaModel>>(KvKey.DISPLAY_IMAGE_MEDIA_LIST)
                    .post(imageList)
            }
        }
    }

    private fun setMediaEmpty(isEmpty: Boolean) {
        if (isEmpty) binding.ivMediaEmpty.visible()
    }


    private fun loadUserInfo() {
        binding.lvLoading.visible()
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
            binding.ivUserNext -> {
                create(this)
                finish()
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