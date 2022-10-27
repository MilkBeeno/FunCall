package com.milk.funcall.account.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.milk.funcall.R
import com.milk.funcall.account.ui.dialog.RechargeDialog
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.pay.GooglePlay
import com.milk.funcall.common.pay.ProductsModel
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.databinding.ActivityRechargeBinding
import com.milk.simple.ktx.*

class RechargeActivity : AbstractActivity() {
    private val binding by lazy { ActivityRechargeBinding.inflate(layoutInflater) }
    private val googlePlay by lazy { GooglePlay() }
    private var productList = mutableListOf<ProductsModel>()
    private val successDialog by lazy { RechargeDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        initializeRecharge()
    }

    private fun initializeRecharge() {
        googlePlay.initialize(this)
        googlePlay.paySuccessListener {
            FireBaseManager.logEvent(FirebaseKey.SUBSCRIPTION_SUCCESS_SHOW)
            successDialog.show()
        }
        googlePlay.payCancelListener {

        }
        googlePlay.payFailureListener {

        }
        googlePlay.productList.collectLatest(this) {
            productList = it
            if (it.isNotEmpty()) {
                binding.tvWeekPrice.text = it.toList()[0].toString()
            }
            if (it.size > 1) {
                binding.tvYearPrice.text = it.toList()[1].toString()
            }
        }
    }

    override fun onClick(p0: View?) {
        super.onClick(p0)
        when (p0) {
            binding.llWeek -> {
                FireBaseManager.logEvent(FirebaseKey.CLICK_SUBSCRIBE_BY_WEEK)
                updateUI(binding.llWeek)
                if (productList.isNotEmpty()) {
                    productList[0].productDetails?.let {
                        googlePlay.launchPurchase(this, it)
                    }
                }
            }
            binding.clYear -> {
                FireBaseManager.logEvent(FirebaseKey.CLICK_SUBSCRIBE_BY_YEAR)
                updateUI(binding.clYear)
                if (productList.size > 1) {
                    productList[1].productDetails?.let {
                        googlePlay.launchPurchase(this, it)
                    }
                }
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