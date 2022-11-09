package com.milk.funcall.user.ui.dialog

import android.view.Gravity
import androidx.fragment.app.FragmentActivity
import com.milk.funcall.common.ui.dialog.SimpleDialog
import com.milk.funcall.databinding.DialogViewAdBinding
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.constrant.FirebaseKey

class ViewAdDialog(activity: FragmentActivity) : SimpleDialog<DialogViewAdBinding>(activity) {
    private var confirmRequest: (() -> Unit)? = null

    init {
        setGravity(Gravity.CENTER)
        setWidthMatchParent(true)
        setCanceledOnTouchOutside(true)
        binding.tvCancel.setOnClickListener {
            FireBaseManager
                .logEvent(FirebaseKey.CLICK_CANCEL_SHOW_CONTACT_DOUBLE_CHECK)
            dismiss()
        }
        binding.tvConfirm.setOnClickListener {
            FireBaseManager
                .logEvent(FirebaseKey.CLICK_CONFIRM_CONTACT_DOUBLE_CHECK)
            confirmRequest?.invoke()
            dismiss()
        }
    }

    internal fun setOnConfirmRequest(confirmRequest: () -> Unit) {
        this.confirmRequest = confirmRequest
    }

    override fun getViewBinding(): DialogViewAdBinding {
        return DialogViewAdBinding.inflate(activity.layoutInflater)
    }
}