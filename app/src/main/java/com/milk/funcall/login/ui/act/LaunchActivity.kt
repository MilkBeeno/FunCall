package com.milk.funcall.login.ui.act

import android.animation.Animator
import android.os.Bundle
import com.milk.funcall.common.constrant.KvKey
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityLaunchBinding
import com.milk.funcall.main.ui.act.MainActivity
import com.milk.simple.ktx.gone
import com.milk.simple.ktx.immersiveStatusBar
import com.milk.simple.ktx.viewBinding
import com.milk.simple.ktx.visible
import com.milk.simple.mdr.KvManger

class LaunchActivity : AbstractActivity() {
    private val binding by viewBinding<ActivityLaunchBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        immersiveStatusBar()
        initializeView()
    }

    private fun initializeView() {
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
                binding.firstLottieView.clearAnimation()
            }
        })
        binding.secondLottieView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) = Unit
            override fun onAnimationCancel(p0: Animator?) = Unit
            override fun onAnimationRepeat(p0: Animator?) = Unit
            override fun onAnimationEnd(p0: Animator?) {
                binding.secondLottieView.clearAnimation()
                val isLogin = KvManger.getBoolean(KvKey.USER_IS_LOGGED)
                if (isLogin)
                    MainActivity.create(this@LaunchActivity)
                else
                    GenderActivity.create(this@LaunchActivity)
                this@LaunchActivity.finish()
            }
        })
    }
}