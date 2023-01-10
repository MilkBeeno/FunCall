package com.milk.funcall.square.ui.dialog

import android.view.Gravity
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import com.milk.funcall.common.ui.dialog.SimpleDialog
import com.milk.funcall.databinding.DialogSquareRulesBinding

class SquareRulesDialog(activity: FragmentActivity) :
    SimpleDialog<DialogSquareRulesBinding>(activity) {
    init {
        setGravity(Gravity.CENTER)
        setWidthMatchParent(true)
        binding.ivClose.setOnClickListener { dismiss() }
    }

    override fun getViewBinding(): DialogSquareRulesBinding {
        return DialogSquareRulesBinding.inflate(LayoutInflater.from(activity))
    }
}