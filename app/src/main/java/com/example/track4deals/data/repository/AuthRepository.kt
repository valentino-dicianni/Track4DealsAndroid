package com.example.track4deals.data.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.track4deals.R
import com.example.track4deals.data.constants.AppConstants.Companion.SERVER_OK
import com.example.track4deals.data.models.*
import com.example.track4deals.services.utils.NoConnectivityException
import com.example.track4deals.internal.UserProvider
import com.example.track4deals.services.AuthService
import com.example.track4deals.services.OffersService
import com.example.track4deals.services.ProductDataService
import com.example.track4deals.services.utils.NoInternetException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.SocketTimeoutException

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class AuthRepository(
    private val userProvider: UserProvider,
    private val authService: AuthService,
    private val offersService: OffersService
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
        try {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                when {
                    task.isSuccessful -> {
                        loginSuccess(result, false)
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

    fun loginWithGoogle(
        idToken: String,
        result: MutableLiveData<LoginResult>
    ) {
        try {
            auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                .addOnCompleteListener { task ->
                    when {
                        task.isSuccessful -> {
                            loginSuccess(result, true)
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

    private fun loginSuccess(result: MutableLiveData<LoginResult>, withUserRegistration: Boolean) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            currentUser.getIdToken(false).result?.token?.let {
                userProvider.loadToken(it)
            }
            currentUser.displayName?.let { userProvider.setUsername(it) }
            currentUser.email?.let { userProvider.setEmail(it) }
            currentUser.photoUrl?.let { userProvider.setProfilePic(it) }
            registerFirebaseToken(
                currentUser.uid,
                withUserRegistration,
                result
            )
        }
    }

    suspend fun registerUser(
        username: String,
        email: String,
        password: String,
        result: MutableLiveData<RegisterResult>
    ) {
        try {
            val serverRes = withContext(Dispatchers.IO) {
                authService.registerNewUserAsync(username, password, email)
            }.await()
            if (serverRes.ok == SERVER_OK) {
                result.value = RegisterResult(success = true)
            } else result.value = RegisterResult(error = R.string.register_failed)

        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", e.message)
        } catch (e: NoInternetException) {
            Log.e("Connectivity", e.message)
        } catch (e: SocketTimeoutException) {
            Log.e("Connectivity", "TimeOut exception", e)
        } catch (e: HttpException) {
            if (e.code() == 500) {
                result.value = RegisterResult(error = R.string.emailRegError)
            }
        }
    }


    private fun registerFirebaseToken(
        uid: String,
        withUserRegistration: Boolean,
        result: MutableLiveData<LoginResult>
    ) {
        messaging.token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            Log.w(TAG, "Fetching FCM registration token: $token", task.exception)
            try {
                GlobalScope.launch(Dispatchers.Main) {
                    if (withUserRegistration)
                        authService.registerNewUserGoogleAsync(uid).await()
                    if (token != null) {
                        authService.registerFirebaseTokenAsync(token).await()
                        Log.d("MyFirebaseMessagingService", "Sent token to Track4Deals Server")
                    }
                    val res = offersService.getAllTrackingAsync().await()
                    res.response?.let { userProvider.setNumTracking(it.size) }
                    result.value = LoginResult(
                        LoggedInUserView(
                            userProvider.getUserName(),
                            userProvider.getProfilePic()
                        )
                    )
                }
            } catch (e: NoConnectivityException) {
                Log.e("Connectivity", e.message)
            } catch (e: NoInternetException) {
                Log.e("Connectivity", e.message)
            } catch (e: SocketTimeoutException) {
                Log.e("Connectivity", "TimeOut exception", e)
            } catch (e: HttpException) {
                if (e.code() == 500) {
                    Log.d(TAG, "registerFirebaseToken: ERRORE: email già registrata firebase")
                }
            }
        })
    }


    fun updateUsername(
        username: String,
        _usernameChangeRes: MutableLiveData<FirebaseOperationResponse>
    ) {
        userProvider.updateUsername(username, _usernameChangeRes)
    }

    fun updatePicture(uri: Uri, _pictureChangeRes: MutableLiveData<FirebaseOperationResponse>) {
        userProvider.updatePicture(uri, _pictureChangeRes)
    }

    suspend fun updateEmail(
        email: String,
        password: String,
        _emailChangeRes: MutableLiveData<FirebaseOperationResponse>
    ) {
        return withContext(Dispatchers.IO) {
            userProvider.updateEmail(email, password, _emailChangeRes)
        }
    }

    fun updatePassword(
        oldpass: String,
        newpass: String,
        _passwordChangeRes: MutableLiveData<FirebaseOperationResponse>
    ) {
        userProvider.updatePassword(oldpass, newpass, _passwordChangeRes)
    }

    fun resetPassword(
        email: String,
        _passwordResetRes: MutableLiveData<FirebaseOperationResponse>
    ) {
        userProvider.resetPassword(email, _passwordResetRes)
    }

    suspend fun delete(password: String, _deleteRes: MutableLiveData<FirebaseOperationResponse>) {
        return withContext(Dispatchers.IO) {
            userProvider.delete(password, _deleteRes)
        }
    }
}