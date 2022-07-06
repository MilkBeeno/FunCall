package com.milk.funcall.common.author

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.milk.funcall.R
import com.milk.simple.ktx.string
import com.milk.simple.log.Logger

class GoogleAuth(private val activity: FragmentActivity) : Auth {
    private var firebaseAuth: FirebaseAuth? = null
    private var googleSignInClient: GoogleSignInClient? = null
    private var successRequest: ((token: String) -> Unit)? = null
    private var cancelRequest: (() -> Unit)? = null
    private var failedRequest: (() -> Unit)? = null

    init {
        initialize()
    }

    override fun initialize() {
        firebaseAuth = FirebaseAuth.getInstance()
        googleSignInClient = GoogleSignIn.getClient(
            activity,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.string(R.string.default_web_client_id))
                .requestEmail()
                .build()
        )
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                googleSignInClient?.signOut()?.addOnFailureListener {
                    Logger.d("Google auth signOut error={$it}", "AuthManager")
                }
                firebaseAuth = null
                googleSignInClient = null
            }
        })
    }

    override fun startAuth() {
        val signClient = checkNotNull(googleSignInClient)
        activity.startActivityForResult(signClient.signInIntent, RC_SIGN_IN)
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
        if (requestCode == RC_SIGN_IN) {
            try {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                if (account.idToken == null)
                    failedRequest?.invoke()
                else
                    firebaseAuthWithGoogle(account.idToken.toString())
            } catch (e: ApiException) {
                cancelRequest?.invoke()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(activity) { task ->
                val user = firebaseAuth?.currentUser
                if (task.isSuccessful && user != null) {
                    successRequest?.invoke(user.uid)
                } else failedRequest?.invoke()
            }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}