package com.milk.funcall.user.ui.act

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.R
import com.milk.funcall.account.Account
import com.milk.funcall.ad.AdConfig
import com.milk.funcall.ad.constant.AdCodeKey
import com.milk.funcall.chat.ui.act.ChatMessageActivity
import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.common.media.loader.ImageLoader
import com.milk.funcall.common.media.loader.VideoLoader
import com.milk.funcall.common.paging.SimpleGridDecoration
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.common.ui.manager.NoScrollGridLayoutManager
import com.milk.funcall.databinding.ActivityUserInfoBinding
import com.milk.funcall.login.ui.dialog.LoadingDialog
import com.milk.funcall.user.data.UserInfoModel
import com.milk.funcall.user.ui.adapter.UserImageAdapter
import com.milk.funcall.user.ui.vm.UserInfoViewModel
import com.milk.simple.ktx.*
import kotlinx.coroutines.flow.collectLatest

class UserInfoActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityUserInfoBinding>()
    private val userInfoViewModel by viewModels<UserInfoViewModel>()
    private val userId by lazy { intent.getLongExtra(USER_ID, 0) }
    private val loadingDialog by lazy { LoadingDialog(this, string(R.string.common_loading)) }

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
        binding.basic.llMessage.setOnClickListener(this)
        binding.basic.llFollow.setOnClickListener(this)
        binding.flVideo.setOnClickListener(this)
        binding.mlImage.setOnClickListener(this)
        binding.link.flLinkLocked.setOnClickListener(this)
        binding.link.llViewLink.setOnClickListener(this)
        binding.mlImage.setOnClickRequest {
            loadingDialog.show()
            userInfoViewModel.loadImageAd(this) {
                loadingDialog.dismiss()
                binding.mlImage.gone()
            }
        }
    }

    private fun initializeObserver() {
        launch {
            userInfoViewModel.userInfoFlow.collectLatest {
                when {
                    it != null && it.targetId > 0 -> {
                        binding.lvLoading.gone()
                        binding.llUserNext.visible()
                        binding.basic.root.visible()
                        binding.link.root.visible()
                        binding.llMedia.visible()
                        setUserBasic(it)
                        setUserMedia(it)
                        setUserFollow(it.targetIsFollowed)
                    }
                    it == null -> {
                        showToast(string(R.string.user_info_obtain_failed))
                        finish()
                    }
                }
            }
        }
        launch {
            userInfoViewModel.userFollowedStatusFlow.collectLatest {
                loadingDialog.dismiss()
                if (it != null) {
                    setUserFollow(it)
                    showToast(string(R.string.common_success))
                }
            }
        }
    }

    private fun setUserFollow(isFollowed: Boolean) {
        binding.basic.llFollow.visible()
        val params = binding.basic.ivFollow.layoutParams as LinearLayoutCompat.LayoutParams
        if (isFollowed) {
            binding.basic.tvFollow.gone()
            binding.basic.ivFollow.setImageResource(R.drawable.user_info_followed)
            binding.basic.llFollow.setBackgroundResource(R.drawable.shape_user_info_followed)
            params.marginStart = dp2px(8f).toInt()
            params.marginEnd = dp2px(8f).toInt()
            binding.basic.ivFollow.layoutParams = params
        } else {
            binding.basic.tvFollow.visible()
            binding.basic.ivFollow.setImageResource(R.drawable.user_info_un_follow)
            binding.basic.llFollow.setBackgroundResource(R.drawable.shape_user_info_un_follow)
            params.marginStart = dp2px(10f).toInt()
            params.marginEnd = 0
            binding.basic.ivFollow.layoutParams = params
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUserBasic(userInfo: UserInfoModel) {
        ImageLoader.Builder()
            .loadAvatar(userInfo.targetAvatar, userInfo.targetGender)
            .target(binding.basic.ivUserAvatar)
            .build()
        binding.basic.ivUserGender.updateGender(userInfo.targetGender)
        binding.tvUserName.text = userInfo.targetName
        binding.tvUserId.text = "ID : ".plus(userInfo.targetIdx)
        binding.tvUserBio.text = userInfo.targetBio
        if (userInfo.targetLink.isNotBlank()) {
            if (userInfoViewModel.hasViewedLink)
                binding.link.flLinkLocked.gone()
            else
                binding.link.flLinkLocked.visible()
            binding.link.clLink.visible()
            binding.link.tvNotLink.gone()
            binding.link.tvContact.text = userInfo.targetLink
        } else {
            binding.link.clLink.gone()
            binding.link.tvNotLink.visible()
            binding.link.flLinkLocked.gone()
        }
    }

    private fun setUserMedia(userInfo: UserInfoModel) {
        // 设置 Video 信息
        val userVideoUrl = userInfo.videoConvert()
        if (userVideoUrl.isNotEmpty()) {
            binding.tvVideo.visible()
            binding.flVideo.visible()
            VideoLoader.Builder()
                .request(userVideoUrl)
                .target(binding.ivVideo)
                .build()
        }
        // 设置 Image 信息
        val userImageList = userInfo.imageListConvert()
        if (userImageList.isNotEmpty()) {
            binding.tvImage.visible()
            binding.rvImage.visible()
            if (userInfoViewModel.hasViewedImage)
                binding.mlImage.gone()
            else
                binding.mlImage.visible()
            binding.rvImage.layoutManager = NoScrollGridLayoutManager(this, 2)
            binding.rvImage.addItemDecoration(SimpleGridDecoration(this))
            binding.rvImage.adapter = UserImageAdapter(userImageList) { position ->
                ImageMediaActivity.create(this, userInfo.targetId, userInfo.targetIsBlacked)
                LiveEventBus
                    .get<Pair<Int, MutableList<String>>>(KvKey.DISPLAY_IMAGE_MEDIA_LIST)
                    .post(Pair(position, userImageList))
            }
        } else binding.mlImage.gone()
        if (userVideoUrl.isEmpty() && userImageList.isEmpty()) {
            binding.ivMediaEmpty.visible()
        }
    }

    private fun loadUserInfo() {
        binding.lvLoading.visible()
        userInfoViewModel.getUserTotalInfo(userId)
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.basic.llFollow -> {
                if (Account.userLogged) {
                    loadingDialog.show()
                    userInfoViewModel.changeFollowedStatus()
                } else showToast(string(R.string.common_place_to_login_first))
            }
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
            binding.flVideo -> {
                userInfoViewModel.userInfoFlow.value?.let {
                    VideoMediaActivity
                        .create(this, it.videoConvert(), it.targetId, it.targetIsBlacked)
                }
            }
            binding.basic.llMessage -> {
                if (Account.userLogged) {
                    userInfoViewModel.userInfoFlow.value?.let {
                        if (it.targetIsBlacked) return
                        ChatMessageActivity.create(this, it.targetId)
                    }
                } else showToast(string(R.string.common_place_to_login_first))
            }
            binding.link.llViewLink -> {
                if (userInfoViewModel.hasViewedVideo || userInfoViewModel.hasViewedImage) {
                    loadLinkAd()
                } else showToast(string(R.string.user_info_please_view_video_or_image))
            }
        }
    }

    /** 加载获取联系方式激励视频广告 */
    private fun loadLinkAd() {
        try {
            val unitId = AdConfig.getAdvertiseUnitId(AdCodeKey.VIEW_USER_LINK)
            if (unitId.isNotBlank()) {
                loadingDialog.show()
                val adRequest = AdRequest.Builder().build()
                RewardedAd.load(this, unitId, adRequest,
                    object : RewardedAdLoadCallback() {
                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            loadingDialog.dismiss()
                        }

                        override fun onAdLoaded(p0: RewardedAd) {
                            super.onAdLoaded(p0)
                            p0.show(this@UserInfoActivity) {
                                loadingDialog.dismiss()
                                binding.link.flLinkLocked.gone()
                                userInfoViewModel.hasViewedLink = true
                            }
                        }
                    })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val USER_ID = "USER_ID"
        fun create(context: Context, userId: Long = 0) {
            val intent = Intent(context, UserInfoActivity::class.java)
            intent.putExtra(USER_ID, userId)
            context.startActivity(intent)
        }
    }
}