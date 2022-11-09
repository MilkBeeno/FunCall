package com.milk.funcall.user.ui.dialog

import android.view.Gravity
import androidx.fragment.app.FragmentActivity
import com.milk.funcall.common.ui.dialog.SimpleDialog
import com.milk.funcall.databinding.DialogViewLinkBinding

class ViewLinkDialog(activity: FragmentActivity) : SimpleDialog<DialogViewLinkBinding>(activity) {
    private var confirmRequest: (() -> Unit)? = null

    init {
        setGravity(Gravity.CENTER)
        setWidthMatchParent(true)
        setCanceledOnTouchOutside(true)
        binding.ivClose.setOnClickListener { dismiss() }
        binding.tvConfirm.setOnClickListener {
            confirmRequest?.invoke()
            dismiss()
        }
    }

    internal fun setOnConfirmRequest(confirmRequest: () -> Unit) {
        this.confirmRequest = confirmRequest
    }

    override fun getViewBinding(): DialogViewLinkBinding {
        return DialogViewLinkBinding.inflate(activity.layoutInflater)
    }
}