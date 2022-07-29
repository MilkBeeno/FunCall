package com.milk.funcall.login.ui.act

import android.animation.Animator
import android.os.Bundle
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.account.Account
import com.milk.funcall.ad.AdConfig
import com.milk.funcall.ad.AdManager
import com.milk.funcall.ad.constant.AdCodeKey
import com.milk.funcall.app.ui.act.MainActivity
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityLaunchBinding
import com.milk.simple.ktx.*

class LaunchActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityLaunchBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        initializeObserver()
        AdConfig.obtain()
        Account.initialize()
    }

    private fun initializeView() {
        immersiveStatusBar()
        binding.root.navigationBarPadding()
        binding.firstLottieView.setAnimation("launch_first.json")
        binding.secondLottieView.setAnimation("launch_second.json")
        binding.firstLottieView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationCancel(p0: Animator?) = Unit
            override fun onAnimationRepeat(p0: Animator?) = Unit
            override fun onAnimationStart(p0: Animator?) = binding.firstLottieView.visible()
            override fun onAnimationEnd(p0: Animator?) {
                binding.ivFunCall.visible()
                binding.secondLottieView.visible()
                binding.firstLottieView.gone()
            }
        })
        binding.secondLottieView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) = Unit
            override fun onAnimationCancel(p0: Animator?) = Unit
            override fun onAnimationRepeat(p0: Animator?) = Unit
            override fun onAnimationEnd(p0: Animator?) {
                AdManager.showInterstitial(this@LaunchActivity) {
                    if (Account.userLogged || Account.userGender.isNotBlank())
                        MainActivity.create(this@LaunchActivity)
                    else
                        GenderActivity.create(this@LaunchActivity)
                }
                this@LaunchActivity.finish()
            }
        })
    }

    private fun initializeObserver() {
        LiveEventBus.get<Any?>(EventKey.UPDATE_START_AD_UNIT_ID)
            .observe(this) {
                val interstitial = AdConfig.getAdvertiseUnitId(AdCodeKey.APP_START)
                if (interstitial.isNotBlank())
                    AdManager.loadInterstitial(this, interstitial)
            }
    }

    override fun onInterceptKeyDownEvent(): Boolean = true

    override fun onDestroy() {
        super.onDestroy()
        binding.firstLottieView.clearAnimation()
        binding.secondLottieView.clearAnimation()
    }
}