package com.milk.funcall.login.ui.dialog

import android.view.LayoutInflater
import com.milk.funcall.common.ui.AbstractActivity
import com.milk.funcall.common.ui.dialog.AbstractDialog
import com.milk.funcall.databinding.DialogLoadingBinding

class LoadingDialog(activity: AbstractActivity) : AbstractDialog<DialogLoadingBinding>(activity) {
    init {
        binding.loading.setAnimation("loading.json")
    }

    override fun initialize() {
        setDimAmount(0.1f)
        setCanceledOnTouchOutside(true)
    }

    override fun getViewBinding(): DialogLoadingBinding {
        return DialogLoadingBinding.inflate(LayoutInflater.from(activity))
    }
}