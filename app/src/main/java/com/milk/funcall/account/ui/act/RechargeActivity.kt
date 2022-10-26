package com.milk.funcall.account.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.milk.funcall.R
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.pay.GooglePlay
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityRechargeBinding
import com.milk.simple.ktx.color
import com.milk.simple.ktx.immersiveStatusBar
import com.milk.simple.ktx.statusBarPadding
import com.milk.simple.ktx.string

class RechargeActivity : AbstractActivity() {
    private val binding by lazy { ActivityRechargeBinding.inflate(layoutInflater) }
    private val googlePlay by lazy { GooglePlay() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        initializeRecharge()
    }

    private fun initializeRecharge() {
        googlePlay.initialize(this)
        googlePlay.paySuccessListener {

        }
        googlePlay.payCancelListener {

        }
        googlePlay.payFailureListener {

        }
    }

    override fun onClick(p0: View?) {
        super.onClick(p0)
        when (p0) {
            binding.llWeek -> {
                FireBaseManager.logEvent(FirebaseKey.CLICK_SUBSCRIBE_BY_WEEK)
                updateUI(binding.llWeek)
            }
            binding.clYear -> {
                FireBaseManager.logEvent(FirebaseKey.CLICK_SUBSCRIBE_BY_YEAR)
                updateUI(binding.clYear)
                googlePlay.launchPurchase(this, "")
            }
        }
    }

    private fun updateUI(clickView: View) {
        if (clickView == binding.llWeek) {
            binding.llWeek.setBackgroundResource(R.drawable.shape_recharge_options_background_select)
            binding.ivWeek.setImageResource(R.drawable.recharge_options_select)
        } else {
            binding.llWeek.setBackgroundResource(R.drawable.shape_recharge_options_background)
            binding.ivWeek.setImageResource(R.drawable.recharge_options)
        }
        if (clickView == binding.clYear) {
            binding.ivYear.setImageResource(R.drawable.recharge_options_select)
            binding.clYear.setBackgroundResource(R.drawable.shape_recharge_options_background_select)
        } else {
            binding.ivYear.setImageResource(R.drawable.recharge_options)
            binding.clYear.setBackgroundResource(R.drawable.shape_recharge_options_background)
        }
    }

    private fun initializeView() {
        FireBaseManager.logEvent(FirebaseKey.SUBSCRIPTIONS_PAGE_SHOW)
        immersiveStatusBar()
        binding.headerToolbar.statusBarPadding()
        binding.headerToolbar.setTitle(string(R.string.recharge_title), color(R.color.white))
        binding.headerToolbar.showArrowBack(R.drawable.common_arrow_back_white)
        binding.rechargeAdView.showTopOnNativeAd()
        binding.llWeek.setOnClickListener(this)
        binding.clYear.setOnClickListener(this)
    }

    companion object {
        fun create(context: Context) =
            context.startActivity(Intent(context, RechargeActivity::class.java))
    }
}