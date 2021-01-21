package com.example.track4deals.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.track4deals.data.models.LoggedInUser
import com.example.track4deals.data.models.LoggedInUserView
import com.example.track4deals.data.models.LoginResult
import com.example.track4deals.internal.UserProvider
import com.example.track4deals.services.ProductDataService
import com.google.firebase.auth.FirebaseAuth

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(
    private val userProvider: UserProvider,
    private val productDataService: ProductDataService

) {
    private lateinit var auth: FirebaseAuth

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    init {
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
                                userProvider.loadToken()
                            }
                            setLoggedInUser(
                                LoggedInUser(
                                    currentUser.uid,
                                    currentUser.displayName!!
                                )
                            )
                            result.value = LoginResult(success = currentUser.displayName?.let {
                                LoggedInUserView(displayName = it)
                            })
                        }
                    }
                }
            }
        } catch (e: Throwable) {
            Log.d("Error", e.toString())
        }
    }

    suspend fun updateTracking() {
        productDataService.getTracking()
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
    }

}