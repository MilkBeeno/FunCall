package com.milk.funcall.account.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.R
import com.milk.funcall.account.Account
import com.milk.funcall.account.ui.dialog.RechargeSuccessDialog
import com.milk.funcall.app.AppConfig
import com.milk.funcall.common.ad.AdConfig
import com.milk.funcall.common.ad.AdManager
import com.milk.funcall.common.constrant.AdCodeKey
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.pay.PayManager
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.common.ui.dialog.SubsDiscountDialog
import com.milk.funcall.databinding.ActivityRechargeBinding
import com.milk.funcall.login.ui.dialog.LoadingDialog
import com.milk.simple.ktx.*

class RechargeActivity : AbstractActivity() {
    private val binding by lazy { ActivityRechargeBinding.inflate(layoutInflater) }
    private val loadingDialog by lazy { LoadingDialog(this) }
    private val rechargeSuccessDialog by lazy { RechargeSuccessDialog(this) }
    private val subsDiscountDialog by lazy {
        SubsDiscountDialog(this, SubsDiscountDialog.Source.Recharge)
    }
    private var adView: View? = null
    private var rechargePageInitialized: Boolean = false
    private var cancelRecharge: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        initializeObserver()
        initializeRecharge()
    }

    private fun initializeView() {
        FireBaseManager.logEvent(FirebaseKey.SUBSCRIPTIONS_PAGE_SHOW)
        immersiveStatusBar()
        binding.headerToolbar.statusBarPadding()
        binding.headerToolbar.setTitle(string(R.string.recharge_title), color(R.color.white))
        binding.headerToolbar.showArrowBack(R.drawable.common_arrow_back_white)
        binding.llWeek.setOnClickListener(this)
        binding.clYear.setOnClickListener(this)
        subsDiscountDialog.setOnConfirmListener {
            PayManager.googlePay.payProduct(this, AppConfig.subsYearDiscountOriginId)
        }
    }

    private fun initializeAdView() {
        try {
            if (adView?.parent != null) return
            val adUnitId = AdConfig.getAdvertiseUnitId(AdCodeKey.RECHARGE_BOTTOM_AD)
            if (adUnitId.isNotBlank() && AdConfig.adCancelType != 2) {
                FireBaseManager.logEvent(FirebaseKey.MAKE_AN_AD_REQUEST_7)
                adView = AdManager.loadBannerAd(activity = this,
                    adUnitId = adUnitId,
                    loadFailureRequest = {
                        FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_FAILED_7, adUnitId, it)
                    },
                    loadSuccessRequest = {
                        FireBaseManager.logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED_7)
                    },
                    showFailureRequest = {
                        FireBaseManager.logEvent(FirebaseKey.AD_SHOW_FAILED_7, adUnitId, it)
                    },
                    showSuccessRequest = {
                        FireBaseManager.logEvent(FirebaseKey.THE_AD_SHOW_SUCCESS_7)
                    },
                    clickRequest = {
                        FireBaseManager.logEvent(FirebaseKey.CLICK_AD_4)
                    })
                binding.root.addView(adView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initializeObserver() {
        Account.userSubscribeFlow.collectLatest(this) {
            if (rechargePageInitialized) {
                loadingDialog.dismiss()
                if (it) rechargeSuccessDialog.show()
                if (it && AdConfig.adCancelType == 2 && adView?.parent != null) {
                    binding.root.removeView(adView)
                } else initializeAdView()
            } else initializeAdView()
            rechargePageInitialized = true
        }
        // 当超过两个小时自动恢复到元价格上
        LiveEventBus.get<Long>(EventKey.UPDATE_SUBSCRIBE_DISCOUNT_TIME).observe(this) {
            if (it == 0L) {
                cancelRecharge = false
                PayManager.googlePay.getProduct(AppConfig.subsYearId)?.let {
                    binding.tvYearPrice.text = it.productPrice
                }
            }
        }
    }

    private fun initializeRecharge() {
        PayManager.googlePay.paySucceeded { orderId, purchaseToken ->
            FireBaseManager.logEvent(FirebaseKey.SUBSCRIPTION_SUCCESS_SHOW)
            mainScope { loadingDialog.show() }
            PayManager.getPayStatus(orderId, purchaseToken)
        }
        PayManager.googlePay.payCanceled {
            cancelRecharge = true
            updateUI(null)
        }
        PayManager.googlePay.payFailed { updateUI(null) }
        PayManager.googlePay.queryProductStatus.collectLatest(this) { success ->
            if (success) {
                PayManager.googlePay.getProduct(AppConfig.subsWeekId)?.let {
                    binding.tvWeekPrice.text = it.productPrice
                }
                val productId = if (PayManager.isSubscribeDiscountPeriod) {
                    AppConfig.subsYearDiscountId
                } else {
                    AppConfig.subsYearId
                }
                PayManager.googlePay.getProduct(productId)?.let {
                    binding.tvYearPrice.text = it.productPrice
                    if (AppConfig.discountNumber > 0) {
                        binding.tvDiscount.visible()
                        binding.tvDiscount.text = AppConfig.discountNumber.toString()
                            .plus(string(R.string.recharge_discount_number))
                    } else binding.tvDiscount.gone()
                }
            }
        }
    }

    override fun onClick(p0: View?) {
        super.onClick(p0)
        when (p0) {
            binding.llWeek -> {
                FireBaseManager.logEvent(FirebaseKey.CLICK_SUBSCRIBE_BY_WEEK)
                updateUI(binding.llWeek)
                PayManager.googlePay.payProduct(this, AppConfig.subsWeekId)
            }
            binding.clYear -> {
                FireBaseManager.logEvent(FirebaseKey.CLICK_SUBSCRIBE_BY_YEAR)
                updateUI(binding.clYear)
                val productId = if (PayManager.isSubscribeDiscountPeriod) {
                    AppConfig.subsYearDiscountId
                } else {
                    AppConfig.subsYearId
                }
                PayManager.googlePay.payProduct(this, productId)
            }
        }
    }

    private fun updateUI(clickView: View?) {
        if (clickView == binding.llWeek) {
            binding.llWeek.setBackgroundResource(R.drawable.shape_recharge_options_background_select)
            binding.ivWeek.setImageResource(R.drawable.recharge_options_select)
            binding.tvFree.text = string(R.string.recharge_week_desc)
        } else {
            binding.llWeek.setBackgroundResource(R.drawable.shape_recharge_options_background)
            binding.ivWeek.setImageResource(R.drawable.recharge_options)
        }
        if (clickView == binding.clYear) {
            binding.ivYear.setImageResource(R.drawable.recharge_options_select)
            binding.clYear.setBackgroundResource(R.drawable.shape_recharge_options_background_select)
            binding.tvFree.text = string(R.string.recharge_year_desc)
        } else {
            binding.ivYear.setImageResource(R.drawable.recharge_options)
            binding.clYear.setBackgroundResource(R.drawable.shape_recharge_options_background)
        }
    }

    override fun onBackPressed() {
        if (AppConfig.showSubsYearDiscountDialog
            && !PayManager.isSubscribeDiscountPeriod
            && cancelRecharge
        ) {
            PayManager.timer.start()
            PayManager.subscribeDiscountProductTime = System.currentTimeMillis()
            subsDiscountDialog.show()
            PayManager.googlePay.getProduct(AppConfig.subsYearDiscountId)?.let {
                binding.tvYearPrice.text = it.productPrice
            }
        } else super.onBackPressed()
    }

    companion object {
        internal fun create(context: Context) =
            context.startActivity(Intent(context, RechargeActivity::class.java))
    }
}