package com.example.track4deals.data

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.track4deals.data.model.LoggedInUser
import com.example.track4deals.ui.login.LoggedInUserView
import com.example.track4deals.ui.login.LoginResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.io.IOException


/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository() : Activity() {
    private lateinit var auth: FirebaseAuth

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
    }

    fun login(
        email: String,
        password: String,
        result: MutableLiveData<LoginResult>
    ) {
        // handle login
        try {
            auth = FirebaseAuth.getInstance()
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                when {
                    task.isSuccessful -> {
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            result.value =
                                LoginResult(success = currentUser.displayName?.let { LoggedInUserView(displayName = it) })
                        }
                    }
                }
            }
        } catch (e: Throwable) {
            Log.d("Error", e.toString())
        }
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }


    fun logoutFirebase() {
        // TODO: revoke authentication
    }
}