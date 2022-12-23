package com.milk.funcall.common.ui.dialog

import android.view.Gravity
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.funcall.R
import com.milk.funcall.app.AppConfig
import com.milk.funcall.common.constrant.EventKey
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.pay.PayManager
import com.milk.funcall.databinding.DialogSubscribeDiscountBinding
import com.milk.simple.ktx.color
import com.milk.simple.ktx.replaceString
import com.milk.simple.ktx.setSpannableColor
import com.milk.simple.ktx.string

class SubsDiscountDialog(activity: FragmentActivity, val source: Source) :
    SimpleDialog<DialogSubscribeDiscountBinding>(activity) {
    private var clickRequest: (() -> Unit)? = null

    init {
        when (source) {
            Source.UserInfo -> {
                FireBaseManager.logEvent(FirebaseKey.SHOW_PROMOTION_ON_PERSONAL)
            }
            Source.Recharge -> {
                FireBaseManager.logEvent(FirebaseKey.SHOW_PROMOTION_WINDOW_ON_SUBSCRIPTION)
            }
        }
        setGravity(Gravity.CENTER)
        setWidthMatchParent(true)
        setCanceledOnTouchOutside(false)
        binding.tvTitle.text =
            AppConfig.subsYearDiscountScale.toString().plus("%")
        binding.tvOriginPrice.text =
            PayManager.googlePay.getProduct(AppConfig.subsYearDiscountOriginId)
                ?.productPrice.toString()
        val discountPrice = PayManager.googlePay.getProduct(AppConfig.subsYearDiscountId)
            ?.productPrice.toString()
        binding.tvDiscountPrice.text =
            activity.string(R.string.dialog_subscribe_discount_price_desc)
                .replaceString(discountPrice)
        binding.tvDiscountPrice.setSpannableColor(
            Pair(
                discountPrice,
                activity.color(R.color.FFFF466C)
            )
        )
        binding.ivClose.setOnClickListener {
            when (source) {
                Source.UserInfo -> {
                    FireBaseManager.logEvent(FirebaseKey.CLOSE_THE_PROMOTIONAL_ON_PERSONAL)
                }
                Source.Recharge -> {
                    FireBaseManager
                        .logEvent(FirebaseKey.CLOSE_THE_PROMOTIONAL_WINDOW_ON_THE_SUBSCRIPTION)
                }
            }
            dismiss()
        }
        binding.tvPay.setOnClickListener {
            when (source) {
                Source.UserInfo -> {
                    FireBaseManager.logEvent(FirebaseKey.ERSONAL_HOME_PAGE_ANNUAL_PAYMENT_SHOW)
                }
                Source.Recharge -> {
                    val tag =
                        FirebaseKey.SUBSCRIPTION_SHOWING_PAYMENT_ANNUAL_SUBSCRIPTION_PROMOTIONAL_VERSION
                    FireBaseManager.logEvent(tag)
                }
            }
            clickRequest?.invoke()
            dismiss()
        }
        LiveEventBus.get<Long>(EventKey.UPDATE_SUBSCRIBE_DISCOUNT_TIME).observe(activity) {
            if (it > 0) {
                val hour = it / (60 * 60 * 1000)
                binding.tvTimeHour.text = "0".plus(hour)
                val minute = (it - hour * (60 * 60 * 1000)) / (60 * 1000)
                binding.tvTimeMinute.text =
                    if (minute >= 10) minute.toString() else "0".plus(minute)
                val second = (it - hour * 60 * 60 * 1000 - minute * 60 * 1000) / 1000
                binding.tvTimeSecond.text =
                    if (second >= 10) second.toString() else "0".plus(second)
            } else dismiss()
        }
    }

    internal fun setOnConfirmListener(clickRequest: () -> Unit) {
        this.clickRequest = clickRequest
    }

    override fun getViewBinding(): DialogSubscribeDiscountBinding {
        return DialogSubscribeDiscountBinding.inflate(LayoutInflater.from(activity))
    }

    enum class Source { UserInfo, Recharge }
}