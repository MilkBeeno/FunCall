package com.milk.funcall.app.ui.dialog

import android.view.Gravity
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import com.milk.funcall.common.constrant.FirebaseKey
import com.milk.funcall.common.firebase.FireBaseManager
import com.milk.funcall.common.ui.dialog.SimpleDialog
import com.milk.funcall.databinding.DialogNotificationBinding

class NotificationDialog(activity: FragmentActivity, confirmRequest: () -> Unit) :
    SimpleDialog<DialogNotificationBinding>(activity) {

    override fun getViewBinding(): DialogNotificationBinding {
        return DialogNotificationBinding.inflate(LayoutInflater.from(activity))
    }

    init {
        setGravity(Gravity.CENTER)
        setWidthMatchParent(true)
        setCanceledOnTouchOutside(true)
        binding.ivClose.setOnClickListener {
            FireBaseManager.logEvent(FirebaseKey.CLICK_OPEN_NOTIFICATION_POPUP_CANCEL)
            dismiss()
        }
        binding.tvConfirm.setOnClickListener {
            FireBaseManager.logEvent(FirebaseKey.CLICK_OPEN_NOTIFICATION_POPUP_CONFIRM)
            confirmRequest()
            dismiss()
        }
    }
}