package com.milk.funcall.common.author

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.milk.funcall.R
import com.milk.simple.ktx.showToast
import com.milk.simple.ktx.string

class LoginManager(activity: FragmentActivity) {
    var success: ((AuthType, String) -> Unit)? = null
    var cancel: (() -> Unit)? = null
    var failed: (() -> Unit)? = null

    private val googleAuth by lazy {
        GoogleAuth(activity).apply {
            onSuccessListener { success?.invoke(AuthType.Google, it) }
            onCancelListener { cancel?.invoke() }
            onFailedListener {
                failed?.invoke()
                activity.showToast(activity.string(R.string.login_google_auth_failed))
            }
        }
    }
    private val facebookAuth by lazy {
        FacebookAuth(activity).apply {
            onSuccessListener { success?.invoke(AuthType.Facebook, it) }
            onCancelListener { cancel?.invoke() }
            onFailedListener {
                failed?.invoke()
                activity.showToast(activity.string(R.string.login_facebook_auth_failed))
            }
        }
    }

    fun facebookAuth() = facebookAuth.startAuth()

    fun googleAuth() = googleAuth.startAuth()

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        facebookAuth.onActivityResult(requestCode, resultCode, data)
        googleAuth.onActivityResult(requestCode, resultCode, data)
    }
}