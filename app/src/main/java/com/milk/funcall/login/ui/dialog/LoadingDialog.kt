package com.milk.funcall.login.ui.dialog

import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import com.milk.funcall.common.ui.dialog.SimpleDialog
import com.milk.funcall.databinding.DialogLoadingBinding

class LoadingDialog(activity: FragmentActivity) : SimpleDialog<DialogLoadingBinding>(activity) {
    init {
        setDimAmount(0.1f)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        binding.loading.setAnimation("loading.json")
    }

    override fun getViewBinding(): DialogLoadingBinding {
        return DialogLoadingBinding.inflate(LayoutInflater.from(activity))
    }
}