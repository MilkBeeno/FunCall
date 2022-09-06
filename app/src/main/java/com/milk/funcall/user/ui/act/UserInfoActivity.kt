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
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.R
import com.milk.funcall.account.Account
import com.milk.funcall.ad.AdConfig
import com.milk.funcall.ad.AdmobManager
import com.milk.funcall.ad.AdSwitchControl
import com.milk.funcall.ad.constant.AdCodeKey
import com.milk.funcall.chat.ui.act.ChatMessageActivity
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.common.media.loader.ImageLoader
import com.milk.funcall.common.paging.SimpleGridDecoration
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.common.ui.manager.NoScrollGridLayoutManager
import com.milk.funcall.databinding.ActivityUserInfoBinding
import com.milk.funcall.firebase.FireBaseManager
import com.milk.funcall.firebase.constant.FirebaseKey
import com.milk.funcall.login.ui.act.LoginActivity
import com.milk.funcall.login.ui.dialog.LoadingDialog
import com.milk.funcall.user.data.UserInfoModel
import com.milk.funcall.user.ui.adapter.UserImageAdapter
import com.milk.funcall.user.ui.dialog.ViewAdDialog
import com.milk.funcall.user.ui.dialog.ViewLinkDialog
import com.milk.funcall.user.ui.vm.UserInfoViewModel
import com.milk.simple.ktx.*

class UserInfoActivity : AbstractActivity() {
    private val binding by lazy { ActivityUserInfoBinding.inflate(layoutInflater) }
    private val userInfoViewModel by viewModels<UserInfoViewModel>()
    private val userId by lazy { intent.getLongExtra(USER_ID, 0) }
    private val loadingDialog by lazy { LoadingDialog(this, string(R.string.common_loading)) }
    private val viewAdDialog by lazy { ViewAdDialog(this) }
    private val viewLinkDialog by lazy { ViewLinkDialog(this) }

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
        binding.mlImage.setOnClickRequest { loadImageAd() }
    }

    private fun initializeObserver() {
        userInfoViewModel.userInfoFlow.collectLatest(this) {
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
        userInfoViewModel.userFollowedStatusFlow.collectLatest(this) {
            loadingDialog.dismiss()
            if (it != null) {
                setUserFollow(it)
                showToast(string(R.string.common_success))
            }
        }
        LiveEventBus.get<Pair<Boolean, Long>>(EventKey.USER_FOLLOWED_STATUS_CHANGED)
            .observe(this) {
                if (it.second == userId) {
                    setUserFollow(it.first)
                    userInfoViewModel.userInfoFlow.value?.targetIsFollowed = it.first
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
            if (userInfoViewModel.hasViewedLink || !AdSwitchControl.viewUserLink)
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
        /*if (userVideoUrl.isNotEmpty()) {
            binding.tvVideo.visible()
            binding.flVideo.visible()
            VideoLoader.Builder()
                .request(userVideoUrl)
                .target(binding.ivVideo)
                .build()
        }*/
        // 设置 Image 信息
        val userImageList = userInfo.imageListConvert()
        if (userImageList.isNotEmpty()) {
            binding.tvImage.visible()
            binding.rvImage.visible()
            if (userInfoViewModel.hasViewedImage || !AdSwitchControl.viewUserImage)
                binding.mlImage.gone()
            else
                binding.mlImage.visible()
            binding.rvImage.layoutManager = NoScrollGridLayoutManager(this, 2)
            binding.rvImage.addItemDecoration(SimpleGridDecoration(this))
            binding.rvImage.adapter = UserImageAdapter(userImageList) { position ->
                FireBaseManager.logEvent(FirebaseKey.CLICK_PHOTO)
                ImageMediaActivity
                    .create(this, userInfo.targetId, userInfo.targetIsBlacked)
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
                } else {
                    showToast(string(R.string.common_place_to_login_first))
                    LoginActivity.create(this)
                }
            }
            binding.link.tvCopy -> {
                val label = string(R.string.app_name)
                val link = binding.link.tvContact.text.toString()
                val cmb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cmb.setPrimaryClip(ClipData.newPlainText(label, link))
                showToast(string(R.string.user_info_copy_success))
            }
            binding.ivUserNext -> {
                FireBaseManager
                    .logEvent(FirebaseKey.CLICK_THE_NEXT)
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
                FireBaseManager
                    .logEvent(FirebaseKey.CLICK_MESSAGE_ON_PROFILE_PAGE)
                if (Account.userLogged) {
                    userInfoViewModel.userInfoFlow.value?.let {
                        if (it.targetIsBlacked) return
                        ChatMessageActivity.create(this, it.targetId)
                    }
                } else {
                    showToast(string(R.string.common_place_to_login_first))
                    LoginActivity.create(this)
                }
            }
            binding.link.llViewLink -> {
                if (userInfoViewModel.hasViewedVideo || userInfoViewModel.hasViewedImage) {
                    FireBaseManager
                        .logEvent(FirebaseKey.SHOW_CONTACT_POPUP_DOUBLE_CHECK)
                    viewAdDialog.show()
                    viewAdDialog.setOnConfirmRequest { loadLinkAd() }
                } else {
                    FireBaseManager
                        .logEvent(FirebaseKey.SHOW_FIRST_UNLOCK_VIDEO_OR_PICTURE)
                    viewLinkDialog.show()
                    viewLinkDialog.setOnConfirmRequest { loadImageAd() }
                }
            }
        }
    }

    /** 加载获取联系方式激励视频广告 */
    private fun loadLinkAd() {
        try {
            val adUnitId =
                AdConfig.getAdvertiseUnitId(AdCodeKey.VIEW_USER_LINK)
            loadingDialog.show()
            FireBaseManager.logEvent(FirebaseKey.MAKE_AN_AD_REQUEST_6)
            AdmobManager.loadIncentiveVideoAd(
                activity = this,
                adUnitId = adUnitId,
                loadFailedRequest = {
                    FireBaseManager
                        .logEvent(FirebaseKey.AD_REQUEST_FAILED_6, adUnitId, it)
                    loadingDialog.dismiss()
                },
                loadSuccessRequest = {
                    FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED_6)
                },
                showFailedRequest = {
                    FireBaseManager
                        .logEvent(FirebaseKey.AD_SHOW_FAILED_6, adUnitId, it)
                },
                showSuccessRequest = {
                    FireBaseManager.logEvent(FirebaseKey.THE_AD_SHOW_SUCCESS_6)
                    loadingDialog.dismiss()
                    binding.link.flLinkLocked.gone()
                    userInfoViewModel.hasViewedLink = true
                },
                clickRequest = {
                    FireBaseManager.logEvent(FirebaseKey.CLICK_AD_6)
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** 获取个人相册插页广告 */
    private fun loadImageAd() {
        loadingDialog.show()
        userInfoViewModel.loadImageAd(
            activity = this,
            failure = {
                loadingDialog.dismiss()
            },
            success = {
                loadingDialog.dismiss()
                binding.mlImage.gone()
            })
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