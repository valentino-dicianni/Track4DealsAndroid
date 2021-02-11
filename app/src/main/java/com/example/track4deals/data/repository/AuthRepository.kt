package com.example.track4deals.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.example.track4deals.R
import com.example.track4deals.data.constants.AppConstants.Companion.SERVER_OK
import com.example.track4deals.data.models.*
import com.example.track4deals.services.utils.NoConnectivityException
import com.example.track4deals.internal.UserProvider
import com.example.track4deals.services.AuthService
import com.example.track4deals.services.OffersService
import com.example.track4deals.services.utils.NoInternetException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.fragment_login.*
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
    private val offersService: OffersService,
    private val context: Context
) {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var messaging: FirebaseMessaging = FirebaseMessaging.getInstance()
    private var sharedPref: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)


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
                        with(sharedPref.edit()) {
                            putString("userPass", password)
                            putBoolean("isLoggedWithGoogle", false)
                            apply()
                        }
                        userProvider.setPass(password)
                        userProvider.setGoogleLogin(false)
                        userProvider.setGoogleAuthToken("")
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

                            with(sharedPref.edit()) {
                                putBoolean("isLoggedWithGoogle", true)
                                putString("googleTokenId", idToken)
                                apply()
                            }
                            userProvider.setGoogleLogin(true)
                            userProvider.setGoogleAuthToken(idToken)
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
                userProvider.loadJwtToken(it)
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

    /**
     * Ask Firebase to change username
     * @param username username to be changed
     * @param _usernameChangeRes liveData updated with the result of the request
     */
    fun updateUsername(
        username: String,
        _usernameChangeRes: MutableLiveData<FirebaseOperationResponse>
    ) {
        val profileUpdates = userProfileChangeRequest {
            displayName = username
        }

        auth.currentUser!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userProvider.setUsername(username)
                    _usernameChangeRes.postValue(
                        FirebaseOperationResponse(
                            true,
                            FirebaseOperation.UPDATEUSERNAME,
                            ""
                        )
                    )
                } else _usernameChangeRes.postValue(
                    FirebaseOperationResponse(
                        false,
                        FirebaseOperation.UPDATEUSERNAME,
                        task.exception?.message.toString()
                    )
                )
            }
    }

    /**
     * Ask Firebase to update profile picture uri
     * @param pic new picture uri
     * @param _pictureChangeRes liveData updated with the result of the request
     */
    fun updatePicture(
        pic: Uri,
        _pictureChangeRes: MutableLiveData<FirebaseOperationResponse>
    ) {
        val profileUpdates = userProfileChangeRequest {
            photoUri = pic
        }

        auth.currentUser!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _pictureChangeRes.postValue(
                        FirebaseOperationResponse(
                            true,
                            FirebaseOperation.UPDATEPIC,
                            ""
                        )
                    )
                    userProvider.setProfilePic(pic)
                } else _pictureChangeRes.postValue(
                    FirebaseOperationResponse(
                        false,
                        FirebaseOperation.UPDATEPIC,
                        task.exception?.message.toString()
                    )
                )
            }

    }

    /**
     * Ask Firebase to update profile email
     * Since it's a sensitive operation password confirmation is required
     * @param email new email
     * @param password user password
     * @param _emailChangeRes liveData updated with the result of the request
     */
    fun updateEmail(
        email: String,
        _emailChangeRes: MutableLiveData<FirebaseOperationResponse>
    ) {
        val credential = if (userProvider.isLoggedWithGoogle()) {
            GoogleAuthProvider.getCredential(userProvider.getGoogleAuthToken(), null)
        } else {
            EmailAuthProvider.getCredential(userProvider.getEmail(), userProvider.getPass())
        }

        auth.currentUser!!.reauthenticate(credential).addOnCompleteListener() {

            if (it.isSuccessful) {
                auth.currentUser!!.updateEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            userProvider.setEmail(email)
                            _emailChangeRes.postValue(
                                FirebaseOperationResponse(
                                    true,
                                    FirebaseOperation.UPDATEEMAIL,
                                    ""
                                )
                            )
                        } else _emailChangeRes.postValue(
                            FirebaseOperationResponse(
                                false,
                                FirebaseOperation.UPDATEEMAIL,
                                task.exception?.message.toString()
                            )
                        )
                    }
            } else _emailChangeRes.postValue(
                FirebaseOperationResponse(
                    false,
                    FirebaseOperation.UPDATEEMAIL,
                    it.exception?.message.toString()
                )
            )

        }

    }


    /**
     * Reauthenticate user and then ask Firebase to change password
     * @param oldpass old password
     * @param newpass new password
     * @param _passwordChangeRes liveData updated with the result of the request
     */
    fun updatePassword(
        oldpass: String,
        newpass: String,
        _passwordChangeRes: MutableLiveData<FirebaseOperationResponse>
    ) {
        val credential: AuthCredential =
            EmailAuthProvider.getCredential(userProvider.getEmail(), oldpass)

        auth.currentUser!!.reauthenticate(credential).addOnCompleteListener() {

            if (it.isSuccessful) {
                auth.currentUser!!.updatePassword(newpass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){

                            with(sharedPref.edit()) {
                                putString("userPass", newpass)
                                apply()
                            }

                            userProvider.setPass(newpass)

                            _passwordChangeRes.postValue(
                                FirebaseOperationResponse(
                                    true,
                                    FirebaseOperation.UPDATEPASSWORD,
                                    ""
                                )
                            )
                        } else _passwordChangeRes.postValue(
                            FirebaseOperationResponse(
                                false,
                                FirebaseOperation.UPDATEPASSWORD,
                                task.exception?.message.toString()
                            )
                        )

                    }
            } else _passwordChangeRes.postValue(
                FirebaseOperationResponse(
                    false,
                    FirebaseOperation.UPDATEPASSWORD,
                    it.exception?.message.toString()
                )
            )
        }
    }

    /**
     * Ask Firebase to reset users password
     * An email will be sent to given address with reset password instruction
     * @param email email of the account to be resetted
     * @param _pictureChangeRes liveData updated with the result of the request
     */
    fun resetPassword(
        email: String,
        _passwordResetRes: MutableLiveData<FirebaseOperationResponse>
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                    _passwordResetRes.postValue(
                        FirebaseOperationResponse(
                            true,
                            FirebaseOperation.RESETPASSWORD,
                            ""
                        )
                    )
                else _passwordResetRes.postValue(
                    FirebaseOperationResponse(
                        false,
                        FirebaseOperation.RESETPASSWORD,
                        task.exception?.message.toString()
                    )
                )
            }
    }

    /**
     * Ask Firebase to delete current user profile
     * Since it's a sensitive operation password confirmation is required
     * @param password user password
     * @param _deleteRes liveData updated with the result of the request
     */
    fun delete(_deleteRes: MutableLiveData<FirebaseOperationResponse>) {
        val credential = if (userProvider.isLoggedWithGoogle()) {
            GoogleAuthProvider.getCredential(userProvider.getGoogleAuthToken(), null)
        } else {
            EmailAuthProvider.getCredential(userProvider.getEmail(), userProvider.getPass())
        }

        auth.currentUser!!.reauthenticate(credential).addOnCompleteListener() {

            if (it.isSuccessful) {
                auth.currentUser!!.delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (task.isSuccessful) _deleteRes.postValue(
                                FirebaseOperationResponse(
                                    true,
                                    FirebaseOperation.DELETE,
                                    ""
                                )
                            ) else _deleteRes.postValue(
                                FirebaseOperationResponse(
                                    false,
                                    FirebaseOperation.DELETE,
                                    task.exception?.message.toString()
                                )
                            )
                        }
                    }
            } else _deleteRes.postValue(
                FirebaseOperationResponse(
                    false,
                    FirebaseOperation.DELETE,
                    it.exception?.message.toString()
                )
            )
        }
    }
}