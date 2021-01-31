package com.example.track4deals.data.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.track4deals.R
import com.example.track4deals.data.models.*
import com.example.track4deals.internal.NoConnectivityException
import com.example.track4deals.internal.UserProvider
import com.example.track4deals.services.AuthService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(
    private val userProvider: UserProvider,
    private val authService: AuthService

) {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var messaging: FirebaseMessaging = FirebaseMessaging.getInstance()


    companion object {
        const val TAG = "LoginRepository"
    }

    fun login(
        email: String,
        password: String,
        result: MutableLiveData<LoginResult>
    ) {
        // handle login
        try {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                when {
                    task.isSuccessful -> {
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            currentUser.getIdToken(false).result?.token?.let {
                                userProvider.loadToken(it)
                            }
                            currentUser.displayName?.let { userProvider.setUsername(it) }
                            //currentUser.photoUrl?.let { userProvider.setProfilePic(it) }

                            result.value = LoginResult(
                                LoggedInUserView(
                                    userProvider.getUserName(),
                                    userProvider.getProfilePic()
                                )
                            )
                            registerFirebaseToken()

                        }
                    }
                    else -> {
                        result.value = LoginResult(error = R.string.login_failed)
                    }
                }

            }
        } catch (e: Throwable) {
            Log.d("Error", e.toString())
        }
    }

    private fun registerFirebaseToken() {
        messaging.token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            Log.w(TAG, "Fetching FCM registration token: $token", task.exception)
            try {
                GlobalScope.launch(Dispatchers.IO) {
                    if (token != null) {
                        authService.registerFirebaseToken(token)
                    }
                    Log.d("MyFirebaseMessagingService", "Sent token to Track4Deals Server")
                }
            } catch (e: NoConnectivityException) {
                Log.e("Connectivity", "NO internet connection", e)
            } catch (e: SocketTimeoutException) {
                Log.e("Connectivity", "TimeOut exception", e)
            }
        })
    }


    suspend fun updateUsername(username: String): LiveData<FirebaseOperationResponse> {
        return withContext(Dispatchers.IO) {
            userProvider.updateUsername(username)
            return@withContext userProvider.firebaseRespone
        }
    }

    suspend fun updatePicture(uri: Uri): LiveData<FirebaseOperationResponse> {
        return withContext(Dispatchers.IO) {
            userProvider.updatePicture(uri)
            return@withContext userProvider.firebaseRespone
        }
    }

    suspend fun updateEmail(email: String, password: String): LiveData<FirebaseOperationResponse> {
        return withContext(Dispatchers.IO) {
            userProvider.updateEmail(email, password)
            return@withContext userProvider.firebaseRespone
        }
    }

    suspend fun updatePassword(pass: String): LiveData<FirebaseOperationResponse> {
        return withContext(Dispatchers.IO) {
            userProvider.updatePassword(pass)
            return@withContext userProvider.firebaseRespone
        }
    }

    suspend fun resetPassword(email: String): LiveData<FirebaseOperationResponse> {
        return withContext(Dispatchers.IO) {
            userProvider.resetPassword(email)
            return@withContext userProvider.firebaseRespone
        }
    }

    suspend fun delete(): LiveData<FirebaseOperationResponse> {
        return withContext(Dispatchers.IO) {
            userProvider.delete()
            return@withContext userProvider.firebaseRespone
        }
    }

}