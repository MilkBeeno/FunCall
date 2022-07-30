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
import com.milk.simple.ktx.gone
import com.milk.simple.ktx.immersiveStatusBar
import com.milk.simple.ktx.navigationBarPadding
import com.milk.simple.ktx.visible

class LaunchActivity : AbstractActivity() {
    private val binding by lazy { ActivityLaunchBinding.inflate(layoutInflater) }

    // 是否已经达到可以显示广告的条件
    private var isDisplayTheAd: Boolean = false

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
        binding.firstLottieView.addAnimatorListener(
            object : Animator.AnimatorListener {
                override fun onAnimationCancel(p0: Animator?) = Unit
                override fun onAnimationRepeat(p0: Animator?) = Unit
                override fun onAnimationStart(p0: Animator?) {
                    binding.firstLottieView.visible()
                }

                override fun onAnimationEnd(p0: Animator?) {
                    if (isDisplayTheAd) {
                        // 1.广告加载完成、达到播放条件播放动画
                        showInterstitial()
                    } else {
                        // 2. 广告加载未完成、当广告加载完成时、达到播放广告条件
                        adFailedDisplay()
                        isDisplayTheAd = true
                    }
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

    /** 运用启动开始加载广告、若加载成功且达到展示条件、则展示广告 */
    private fun initializeObserver() {
        LiveEventBus.get<Any?>(EventKey.UPDATE_START_AD_UNIT_ID)
            .observe(this) {
                val interstitial =
                    AdConfig.getAdvertiseUnitId(AdCodeKey.APP_START)
                AdManager.loadInterstitial(
                    context = this,
                    adUnitId = interstitial,
                    successRequest = {
                        if (isDisplayTheAd) {
                            // 1.第一个动画播放完成、可以直接展示广告
                            showInterstitial()
                        } else {
                            // 2.第一个动画未完成、第一个动画结束达到播放动画的条件
                            isDisplayTheAd = true
                        }
                    })
            }
    }

    private fun showInterstitial() {
        AdManager.showInterstitial(this) {
            if (Account.userLogged || Account.userGender.isNotBlank())
                MainActivity.create(this)
            else
                GenderActivity.create(this)
        }
        finish()
    }

    private fun adFailedDisplay() {
        binding.ivFunCall.visible()
        binding.secondLottieView.visible()
        binding.firstLottieView.gone()
    }

    override fun onInterceptKeyDownEvent(): Boolean = true

    override fun onDestroy() {
        super.onDestroy()
        binding.firstLottieView.clearAnimation()
        binding.secondLottieView.clearAnimation()
    }
}