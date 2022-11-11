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
import com.milk.funcall.account.ui.act.RechargeActivity
import com.milk.funcall.app.AppConfig
import com.milk.funcall.chat.ui.act.ChatMessageActivity
import com.milk.funcall.common.ad.AdConfig
import com.milk.funcall.common.author.DeviceManager
import com.milk.funcall.common.constrant.AdCodeKey
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.media.loader.ImageLoader
import com.milk.funcall.common.paging.SimpleGridDecoration
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.common.ui.manager.NoScrollGridLayoutManager
import com.milk.funcall.databinding.ActivityUserInfoBinding
import com.milk.funcall.login.ui.act.LoginActivity
import com.milk.funcall.login.ui.dialog.LoadingDialog
import com.milk.funcall.user.data.UserInfoModel
import com.milk.funcall.user.status.UnlockType
import com.milk.funcall.user.ui.adapter.UserImageAdapter
import com.milk.funcall.user.ui.dialog.ReportDialog
import com.milk.funcall.user.ui.dialog.ViewAdDialog
import com.milk.funcall.user.ui.dialog.ViewLinkDialog
import com.milk.funcall.user.ui.vm.UserInfoViewModel
import com.milk.simple.ktx.*

class UserInfoActivity : AbstractActivity() {
    private val binding by lazy { ActivityUserInfoBinding.inflate(layoutInflater) }
    private val userInfoViewModel by viewModels<UserInfoViewModel>()
    private val userId by lazy { intent.getLongExtra(USER_ID, 0) }
    private val loadingDialog by lazy { LoadingDialog(this) }
    private val viewAdDialog by lazy { ViewAdDialog(this) }
    private val viewLinkDialog by lazy { ViewLinkDialog(this) }
    private val reportDialog by lazy { ReportDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        initializeObserver()
        loadUserInfo()
    }

    private fun initializeView() {
        immersiveStatusBar()
        binding.flHeader.statusBarPadding()
        binding.root.navigationBarPadding()
        binding.headerToolbar.showArrowBack()
        binding.ivReport.setOnClickListener(this)
        binding.link.tvCopy.setOnClickListener(this)
        binding.ivUserNext.setOnClickListener(this)
        binding.basic.llMessage.setOnClickListener(this)
        binding.basic.llFollow.setOnClickListener(this)
        binding.flVideo.setOnClickListener(this)
        binding.mlImage.setOnClickListener(this)
        binding.link.flLinkLocked.setOnClickListener(this)
        binding.link.llViewLink.setOnClickListener(this)
        binding.mlImage.setOnClickRequest { loadImages() }
        reportDialog.setReportListener {
            loadingDialog.show()
            userInfoViewModel.report(userId, it)
        }
    }

    private fun initializeObserver() {
        userInfoViewModel.loadUserInfoStatusFlow.collectLatest(this) {
            if (it) {
                binding.lvLoading.gone()
                binding.llUserNext.visible()
                binding.basic.root.visible()
                binding.link.root.visible()
                binding.llMedia.visible()
                setUserBasic()
                setUserFollow()
                setUserLink()
                val videoEmpty = setUserVideo()
                val imageEmpty = setUserImage()
                if (videoEmpty && imageEmpty) binding.ivMediaEmpty.visible()
            } else {
                showToast(string(R.string.user_info_obtain_failed))
                finish()
            }
        }
        userInfoViewModel.changeFollowedStatusFlow.collectLatest(this) {
            loadingDialog.dismiss()
            if (it) {
                setUserFollow()
                showToast(string(R.string.common_success))
            }
        }
        userInfoViewModel.changeUnlockStatusFlow.collectLatest(this) {
            loadingDialog.dismiss()
            if (it) {
                val userInfo = userInfoViewModel.getUserInfoModel()
                if (!userInfo.linkUnlocked) {
                    setLinkTimes(userInfo)
                }
                if (!userInfo.imageUnlocked) {
                    binding.mlImage.setMediaTimes(userInfo)
                }
            }
        }
        LiveEventBus.get<Pair<Boolean, Long>>(EventKey.USER_FOLLOWED_STATUS_CHANGED)
            .observe(this) {
                if (it.second == userId) {
                    userInfoViewModel.getUserInfoModel().targetIsFollowed = it.first
                    setUserFollow()
                }
            }
        userInfoViewModel.reportFlow.collectLatest(this) {
            loadingDialog.dismiss()
            if (it) showLongToast(string(R.string.common_report_successful))
        }
    }

    private fun setUserFollow() {
        binding.basic.llFollow.visible()
        val params = binding.basic.ivFollow.layoutParams as LinearLayoutCompat.LayoutParams
        if (userInfoViewModel.getUserInfoModel().targetIsFollowed) {
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
    private fun setUserBasic() {
        val userInfo = userInfoViewModel.getUserInfoModel()
        ImageLoader.Builder()
            .loadAvatar(userInfo.targetAvatar, userInfo.targetGender)
            .target(binding.basic.ivUserAvatar)
            .build()
        binding.basic.ivUserGender.updateGender(userInfo.targetGender)
        binding.tvUserName.text = userInfo.targetName
        binding.tvUserId.text = "ID : ".plus(userInfo.targetIdx)
        binding.tvUserBio.text = userInfo.targetBio
    }

    private fun setUserLink() {
        val userInfo = userInfoViewModel.getUserInfoModel()
        when {
            userInfo.targetLink.isNotBlank() -> {
                binding.link.flLinkLocked.gone()
                if (!Account.userSubscribe) {
                    val adUnitId = AdConfig.getAdvertiseUnitId(AdCodeKey.VIEW_USER_LINK)
                    if (adUnitId.isNotBlank() && !userInfo.linkUnlocked) {
                        setLinkTimes(userInfo)
                        binding.link.flLinkLocked.visible()
                    }
                }
                binding.link.clLink.visible()
                binding.link.tvNotLink.gone()
                binding.link.tvContact.text = userInfo.targetLink
            }
            else -> {
                binding.link.clLink.gone()
                binding.link.tvNotLink.visible()
                binding.link.flLinkLocked.gone()
            }
        }
    }

    private fun setLinkTimes(userInfo: UserInfoModel) {
        val maxTimes = if (userInfo.unlockMethod == 1) {
            binding.link.ivLinkType
                .setImageResource(R.drawable.user_info_media_locked_view)
            AppConfig.freeUnlockTimes
        } else {
            binding.link.ivLinkType
                .setImageResource(R.drawable.user_info_media_locked_view_ad)
            AppConfig.viewAdUnlockTimes
        }
        if (maxTimes < 10) {
            binding.link.tvLinkTimes.visible()
            binding.link.tvLinkTimes.text =
                "(".plus(string(R.string.user_info_unlock_times))
                    .plus(" ")
                    .plus(userInfo.remainUnlockCount)
                    .plus("/")
                    .plus(maxTimes)
                    .plus(")")
        } else binding.link.tvLinkTimes.gone()
    }

    private fun setUserVideo(): Boolean {
        val userInfo = userInfoViewModel.getUserInfoModel()
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
        return userVideoUrl.isBlank()
    }

    private fun setUserImage(): Boolean {
        val userInfo = userInfoViewModel.getUserInfoModel()
        val userImageList = userInfo.imageListConvert()
        when {
            userImageList.isNotEmpty() -> {
                binding.tvImage.visible()
                binding.rvImage.visible()
                binding.mlImage.gone()
                if (!Account.userSubscribe) {
                    val adUnitId = AdConfig.getAdvertiseUnitId(AdCodeKey.VIEW_USER_IMAGE)
                    if (adUnitId.isNotBlank() && !userInfo.imageUnlocked) {
                        binding.mlImage.visible()
                        binding.mlImage.setMediaTimes(userInfo)
                    }
                }
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
            }
            else -> binding.mlImage.gone()
        }
        return userImageList.isEmpty()
    }

    private fun loadUserInfo() {
        binding.lvLoading.visible()
        userInfoViewModel.loadUserInfo(userId, DeviceManager.number)
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.ivReport -> reportDialog.show()
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
                FireBaseManager.logEvent(FirebaseKey.CLICK_THE_NEXT)
                create(this)
                finish()
            }
            binding.flVideo -> {
                userInfoViewModel.getUserInfoModel().let {
                    VideoMediaActivity.create(
                        this,
                        it.videoConvert(),
                        it.targetId,
                        it.targetIsBlacked
                    )
                }
            }
            binding.basic.llMessage -> {
                FireBaseManager
                    .logEvent(FirebaseKey.CLICK_MESSAGE_ON_PROFILE_PAGE)
                if (Account.userLogged) {
                    userInfoViewModel.getUserInfoModel().let {
                        if (it.targetIsBlacked) return
                        ChatMessageActivity.create(this, it.targetId)
                    }
                } else {
                    showToast(string(R.string.common_place_to_login_first))
                    LoginActivity.create(this)
                }
            }
            binding.link.llViewLink -> {
                val userInfo = userInfoViewModel.getUserInfoModel()
                when {
                    userInfo.remainUnlockCount <= 0 -> {
                        RechargeActivity.create(this)
                    }
                    userInfo.videoUnlocked || userInfo.imageUnlocked -> {
                        // 点击直接查看
                        if (userInfo.unlockMethod == 1) {
                            FireBaseManager.logEvent(FirebaseKey.CLICK_FREE_UNLOCK_CONTACT)
                            binding.link.flLinkLocked.gone()
                            userInfoViewModel.changeUnlockStatus(
                                DeviceManager.number,
                                UnlockType.Link.value,
                                userInfo.targetId
                            )
                        } else {
                            // 观看广告后可查看
                            FireBaseManager
                                .logEvent(FirebaseKey.CLICK_AD_TO_UNLOCK_THE_CONTACT_INFORMATION)
                            FireBaseManager
                                .logEvent(FirebaseKey.SHOW_CONTACT_POPUP_DOUBLE_CHECK)
                            viewAdDialog.show()
                            viewAdDialog.setOnConfirmRequest { loadLinkAd() }
                        }
                    }
                    else -> {
                        FireBaseManager
                            .logEvent(FirebaseKey.SHOW_FIRST_UNLOCK_VIDEO_OR_PICTURE)
                        viewLinkDialog.show()
                        viewLinkDialog.setOnConfirmRequest { loadImages() }
                    }
                }
            }
        }
    }

    /** 加载获取联系方式激励视频广告 */
    private fun loadLinkAd() {
        loadingDialog.show()
        val userInfo = userInfoViewModel.getUserInfoModel()
        userInfoViewModel.loadLinkAd(
            activity = this,
            failure = { loadingDialog.dismiss() },
            success = {
                loadingDialog.dismiss()
                binding.link.flLinkLocked.gone()
                userInfoViewModel.changeUnlockStatus(
                    DeviceManager.number,
                    UnlockType.Link.value,
                    userInfo.targetId
                )
            }
        )
    }

    /** 获取个人相册插页广告 */
    private fun loadImages() {
        val userInfo = userInfoViewModel.getUserInfoModel()
        when {
            userInfo.remainUnlockCount <= 0 -> {
                RechargeActivity.create(this)
            }
            userInfo.unlockMethod == 1 -> {
                FireBaseManager.logEvent(FirebaseKey.CLICK_UNLOCK_PHOTO_ALBUM_FOR_FREE)
                binding.mlImage.gone()
                userInfoViewModel.changeUnlockStatus(
                    DeviceManager.number,
                    UnlockType.Image.value,
                    userInfo.targetId
                )
            }
            else -> {
                FireBaseManager.logEvent(FirebaseKey.CLICK_THE_AD_TO_UNLOCK_THE_ALBUM)
                loadingDialog.show()
                userInfoViewModel.loadImageAd(
                    activity = this,
                    failure = { loadingDialog.dismiss() },
                    success = {
                        binding.mlImage.gone()
                        userInfoViewModel.changeUnlockStatus(
                            DeviceManager.number,
                            UnlockType.Image.value,
                            userInfo.targetId
                        )
                    })
            }
        }
    }

    companion object {
        private const val USER_ID = "USER_ID"
        internal fun create(context: Context, userId: Long = 0) {
            val intent = Intent(context, UserInfoActivity::class.java)
            intent.putExtra(USER_ID, userId)
            context.startActivity(intent)
        }
    }
}