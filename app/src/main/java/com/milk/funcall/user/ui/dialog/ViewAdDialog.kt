package com.milk.funcall.user.ui.dialog

import android.view.Gravity
import androidx.fragment.app.FragmentActivity
import com.milk.funcall.common.ui.dialog.SimpleDialog
import com.milk.funcall.databinding.DialogViewAdBinding

class ViewAdDialog(activity: FragmentActivity) : SimpleDialog<DialogViewAdBinding>(activity) {
    private var confirmRequest: (() -> Unit)? = null

    init {
        setGravity(Gravity.CENTER)
        setWidthMatchParent(true)
        setCanceledOnTouchOutside(true)
        binding.tvCancel.setOnClickListener { dismiss() }
        binding.tvConfirm.setOnClickListener {
            confirmRequest?.invoke()
            dismiss()
        }
    }

    fun setOnConfirmRequest(confirmRequest: () -> Unit) {
        this.confirmRequest = confirmRequest
    }

    override fun getViewBinding(): DialogViewAdBinding {
        return DialogViewAdBinding.inflate(activity.layoutInflater)
    }
}