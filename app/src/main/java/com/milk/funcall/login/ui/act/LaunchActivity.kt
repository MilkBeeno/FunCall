package com.milk.funcall.login.ui.act

import android.animation.Animator
import android.os.Bundle
import androidx.activity.viewModels
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.account.Account
import com.milk.funcall.ad.AdConfig
import com.milk.funcall.ad.AdSwitch
import com.milk.funcall.app.ui.act.MainActivity
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityLaunchBinding
import com.milk.funcall.login.ui.vm.LaunchViewModel
import com.milk.simple.ktx.gone
import com.milk.simple.ktx.immersiveStatusBar
import com.milk.simple.ktx.navigationBarPadding
import com.milk.simple.ktx.visible

class LaunchActivity : AbstractActivity() {
    private val binding by lazy { ActivityLaunchBinding.inflate(layoutInflater) }
    private val launchViewModel by viewModels<LaunchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        initializeObserver()
        AdSwitch.obtain()
        AdConfig.obtain()
        Account.initialize()
    }

    private fun initializeView() {
        immersiveStatusBar()
        binding.root.navigationBarPadding()
        binding.firstLottieView.setAnimation("launch_first.json")
        binding.firstLottieView.playAnimation()
        binding.firstLottieView.addAnimatorListener(
            object : Animator.AnimatorListener {
                override fun onAnimationCancel(p0: Animator?) = Unit
                override fun onAnimationRepeat(p0: Animator?) = Unit
                override fun onAnimationStart(p0: Animator?) {
                    binding.firstLottieView.visible()
                }

                override fun onAnimationEnd(p0: Animator?) {
                    launchViewModel.showLaunchAd(
                        activity = this@LaunchActivity,
                        loading = { showNextAnimation() },
                        failure = { toMainOrGenderPage() },
                        success = { toMainOrGenderPage() }
                    )
                }
            })
        binding.secondLottieView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) = Unit
            override fun onAnimationCancel(p0: Animator?) = Unit
            override fun onAnimationRepeat(p0: Animator?) = Unit
            override fun onAnimationEnd(p0: Animator?) {
                launchViewModel.showLaunchAd(
                    activity = this@LaunchActivity,
                    loading = { toMainOrGenderPage() },
                    failure = { toMainOrGenderPage() },
                    success = { toMainOrGenderPage() }
                )
            }
        })
    }

    /** 运用启动开始加载广告、若加载成功且达到展示条件、则展示广告 */
    private fun initializeObserver() {
        LiveEventBus.get<Any?>(EventKey.UPDATE_START_AD_UNIT_ID)
            .observe(this) {
                launchViewModel.loadLaunchAd(
                    activity = this,
                    failure = { toMainOrGenderPage() },
                    success = { toMainOrGenderPage() })
            }
    }

    private fun toMainOrGenderPage() {
        if (Account.userLogged || Account.userGender.isNotBlank())
            MainActivity.create(this)
        else
            GenderActivity.create(this)
        finish()
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