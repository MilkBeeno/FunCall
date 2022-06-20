package com.milk.funcall.common.author

import android.content.Intent
import androidx.fragment.app.FragmentActivity

class AuthLoginManager(activity: FragmentActivity) {

    var cancel: (() -> Unit)? = null
    var failed: (() -> Unit)? = null
    var success: ((String) -> Unit)? = null

    private val facebookAuth by lazy {
        FacebookAuth(activity).apply {
            onCancelListener { cancel?.invoke() }
            onFailedListener { failed?.invoke() }
            onSuccessListener { success?.invoke(it) }
        }
    }

    fun facebookAuth() = facebookAuth.startAuth()

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        facebookAuth.onActivityResult(requestCode, resultCode, data)
    }
}