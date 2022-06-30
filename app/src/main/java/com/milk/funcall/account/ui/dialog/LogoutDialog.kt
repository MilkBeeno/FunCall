package com.milk.funcall.account.ui.dialog

import android.view.Gravity
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import com.milk.funcall.R
import com.milk.funcall.common.ui.dialog.SimpleDialog
import com.milk.funcall.databinding.DialogLogOutBinding

class LogoutDialog(activity: FragmentActivity, clickRequest: () -> Unit) :
    SimpleDialog<DialogLogOutBinding>(activity) {

    init {
        setGravity(Gravity.BOTTOM)
        setWidthMatchParent(true)
        setWindowAnimations(R.style.BottomDialog_Animation)
        binding.tvLogoutCancel.setOnClickListener { dismiss() }
        binding.tvLogoutConfirm.setOnClickListener {
            dismiss()
            clickRequest()
        }
    }

    override fun getViewBinding(): DialogLogOutBinding {
        return DialogLogOutBinding.inflate(LayoutInflater.from(activity))
    }
}