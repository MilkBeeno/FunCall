package com.milk.funcall.common.author

import android.app.Application
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.milk.funcall.BuildConfig
import com.milk.funcall.R
import com.milk.simple.log.Logger
import java.util.ArrayList

class FacebookAuth(private val activity: FragmentActivity) : Auth {
    private val callbackManager by lazy { CallbackManager.Factory.create() }
    private var successRequest: ((token: String) -> Unit)? = null
    private var cancelRequest: (() -> Unit)? = null
    private var failedRequest: (() -> Unit)? = null

    init {
        initialize()
    }

    override fun initialize() {
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    cancelRequest?.invoke()
                }

                override fun onError(error: FacebookException) {
                    failedRequest?.invoke()
                }

                override fun onSuccess(result: LoginResult) {
                    successRequest?.invoke(result.accessToken.token)
                }
            })
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                try {
                    LoginManager.getInstance().logOut()
                } catch (e: Exception) {
                    Logger.e("Facebook Logout Error:$e", "OAuthManager")
                } finally {
                    LoginManager.getInstance().unregisterCallback(callbackManager)
                }
            }
        })
    }

    override fun startAuth() {
        val permissions = ArrayList<String>()
        permissions.add("public_profile")
        LoginManager.getInstance().logInWithReadPermissions(activity, permissions)
    }

    override fun onSuccessListener(success: (String) -> Unit) {
        successRequest = success
    }

    override fun onCancelListener(cancel: () -> Unit) {
        cancelRequest = cancel
    }

    override fun onFailedListener(failed: () -> Unit) {
        failedRequest = failed
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        fun initializeSdk(application: Application) {
            FacebookSdk.sdkInitialize(application)
            AppEventsLogger.activateApp(application)
            FacebookSdk.setIsDebugEnabled(BuildConfig.DEBUG)
            FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS)
            FacebookSdk.setApplicationId(application.resources.getString(R.string.facebook_app_id))
        }
    }
}