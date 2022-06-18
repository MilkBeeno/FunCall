package com.milk.funcall.common.author

import android.content.Intent
import androidx.fragment.app.FragmentActivity

class GoogleAuth(private val activity: FragmentActivity) : Auth {

    override fun initialize() {

    }

    override fun startAuth() {
    }

    override fun onSuccessListener(success: (String) -> Unit) {
    }

    override fun onCancelListener(cancel: () -> Unit) {
    }

    override fun onFailedListener(failed: () -> Unit) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    }
}