package com.milk.funcall.account.ui.dialog

import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import com.milk.funcall.common.ui.dialog.SimpleDialog
import com.milk.funcall.databinding.DialogRechargeSubscribedBinding

class SubscribedDialog(activity: FragmentActivity) :
    SimpleDialog<DialogRechargeSubscribedBinding>(activity) {
    private val handler = Handler(Looper.myLooper() ?: Looper.getMainLooper())

    init {
        setGravity(Gravity.CENTER)
        setWidthMatchParent(true)
        setCanceledOnTouchOutside(true)
        handler.postDelayed({ dismiss() }, 1500)
    }

    override fun getViewBinding(): DialogRechargeSubscribedBinding {
        return DialogRechargeSubscribedBinding.inflate(LayoutInflater.from(activity))
    }
}