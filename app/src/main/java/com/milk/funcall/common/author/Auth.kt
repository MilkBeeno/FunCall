package com.milk.funcall.common.author

import android.content.Intent

interface Auth {
    fun initialize()
    fun startAuth()
    fun onSuccessListener(success: (String) -> Unit)
    fun onCancelListener(cancel: () -> Unit = {})
    fun onFailedListener(failed: () -> Unit = {})
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}