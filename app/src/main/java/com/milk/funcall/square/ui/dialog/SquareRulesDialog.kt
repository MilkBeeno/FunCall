package com.milk.funcall.square.ui.dialog

import android.view.Gravity
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import com.milk.funcall.R
import com.milk.funcall.app.AppConfig
import com.milk.funcall.common.ui.dialog.SimpleDialog
import com.milk.funcall.databinding.DialogSquareRulesBinding
import com.milk.simple.ktx.replaceString
import com.milk.simple.ktx.string

class SquareRulesDialog(activity: FragmentActivity) :
    SimpleDialog<DialogSquareRulesBinding>(activity) {
    init {
        setGravity(Gravity.CENTER)
        setWidthMatchParent(true)
        binding.ivClose.setOnClickListener { dismiss() }
        binding.tvPoint1.text = activity.string(R.string.dialog_square_point_1)
            .replaceString(AppConfig.freeMatchTimes.toString())
        binding.tvPoint3.text = activity.string(R.string.dialog_square_point_3)
            .replaceString(AppConfig.matchAdRewardTime.toString())
    }

    override fun getViewBinding(): DialogSquareRulesBinding {
        return DialogSquareRulesBinding.inflate(LayoutInflater.from(activity))
    }
}