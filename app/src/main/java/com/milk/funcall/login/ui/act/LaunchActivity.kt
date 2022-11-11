package com.milk.funcall.login.ui.act

import android.animation.Animator
import android.os.Bundle
import androidx.activity.viewModels
import com.google.firebase.messaging.Constants
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.account.Account
import com.milk.funcall.app.AppConfig
import com.milk.funcall.app.ui.act.MainActivity
import com.milk.funcall.common.ad.AdConfig
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.pay.PayManager
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityLaunchBinding
import com.milk.funcall.login.ui.vm.LaunchViewModel
import com.milk.funcall.user.ui.act.UserInfoActivity
import com.milk.simple.ktx.gone
import com.milk.simple.ktx.immersiveStatusBar
import com.milk.simple.ktx.navigationBarPadding
import com.milk.simple.ktx.visible
import com.milk.simple.mdr.KvManger

class LaunchActivity : AbstractActivity() {
    private val binding by lazy { ActivityLaunchBinding.inflate(layoutInflater) }
    private val launchViewModel by viewModels<LaunchViewModel>()
    private var isNotLoaded = !AdConfig.isLoadedAds()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        initializeNotification()
        initializeObserver()
        if (isNotLoaded) {
            uploadDeviceInfo()
            PayManager.getPayStatus()
            AppConfig.obtain()
            AdConfig.obtain()
            Account.initialize()
            checkIsNewClient()
        }
    }

    private fun initializeView() {
        immersiveStatusBar()
        binding.root.navigationBarPadding()
        binding.firstLottieView.setAnimation("launch_first.json")
        binding.firstLottieView.playAnimation()
        binding.firstLottieView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationCancel(p0: Animator?) = Unit
            override fun onAnimationRepeat(p0: Animator?) = Unit
            override fun onAnimationStart(p0: Animator?) {
                binding.firstLottieView.visible()
            }

            override fun onAnimationEnd(p0: Animator?) {
                showNextAnimation()
            }
        })
    }

    private fun initializeNotification() {
        var map = mutableMapOf<String, String>()
        intent.extras?.let {
            map = Constants.MessagePayloadKeys.extractDeveloperDefinedPayload(intent.extras)
        }
        if (map.containsKey("userId")) {
            val userId = map["userId"]?.toLong()
            if (userId != null && userId > 0) {
                UserInfoActivity.create(this, userId)
                finish()
            }
        }
    }

    /** 运用启动开始加载广告、若加载成功且达到展示条件、则展示广告 */
    private fun initializeObserver() {
        // launchViewModel.getHasKey(this)
        LiveEventBus.get<Any?>(EventKey.UPDATE_START_AD_UNIT_ID).observe(this) {
            launchViewModel.loadLaunchAd(this) { toMainOrGenderPage() }
        }
    }

    private fun uploadDeviceInfo() {
        launchViewModel.uploadDeviceInfo()
    }

    private fun checkIsNewClient() {
        val isNewClient = KvManger.getBoolean(KvKey.CHECK_IS_NEW_CLIENT, true)
        if (isNewClient) {
            KvManger.put(KvKey.CHECK_IS_NEW_CLIENT, false)
            FireBaseManager.logEvent(FirebaseKey.FIRST_OPEN)
        }
    }

    private fun toMainOrGenderPage() {
        finish()
        if (Account.userLogged || Account.userGender.isNotBlank()) MainActivity.create(this)
        else GenderActivity.create(this)
    }

    private fun showNextAnimation() {
        binding.ivFunCall.visible()
        binding.firstLottieView.gone()
        binding.secondLottieView.visible()
        binding.secondLottieView.setAnimation("launch_second.json")
        binding.secondLottieView.playAnimation()
    }

    override fun onInterceptKeyDownEvent(): Boolean = true

    override fun onDestroy() {
        super.onDestroy()
        binding.firstLottieView.clearAnimation()
        binding.secondLottieView.clearAnimation()
    }
}