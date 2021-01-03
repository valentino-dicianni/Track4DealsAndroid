package com.example.track4deals.data

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.track4deals.data.models.LoggedInUser
import com.example.track4deals.data.models.LoggedInUserView
import com.example.track4deals.data.models.LoginResult
import com.google.firebase.auth.FirebaseAuth


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
                            currentUser.getIdToken(false).result?.token?.let {
                                Log.d("LOGIN TOKEN: ", it)
                            }
                            setLoggedInUser(LoggedInUser(currentUser.uid, currentUser.displayName!!))
                            result.value = LoginResult(success = currentUser.displayName?.let { LoggedInUserView(displayName = it) })
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

}