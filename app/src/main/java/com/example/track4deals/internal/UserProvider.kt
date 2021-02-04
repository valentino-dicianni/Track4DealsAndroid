package com.example.track4deals.internal

import android.net.Uri
import android.util.Log
import android.util.LogPrinter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.example.track4deals.data.models.FirebaseOperation
import com.example.track4deals.data.models.FirebaseOperationResponse
import com.example.track4deals.data.models.ServerResponse
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.userProfileChangeRequest
import java.lang.Exception
import java.security.AuthProvider

class UserProvider {
    private var token: String = ""
    private var username: String = ""
    private var email: String = ""
    private var profilePic: Uri = Uri.EMPTY
    private lateinit var caregoty_list: Array<String?>
    private var phone: String = ""
    private var numTracking: Int = 0
    private var loading = MutableLiveData<Boolean>()

    private var firebase = FirebaseAuth.getInstance()

    init {
        getToken {
            token = it
            loading.postValue(true)
        }
    }

    val loadingComplete = loading.switchMap {
        liveData {
            emit(it)
        }
    }


    fun getToken() = token

    fun loadToken(token: String) {
        this.token = token
    }

    fun setUsername(username: String) {
        this.username = username
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun setPic(pic: Uri) {
        this.profilePic = pic
    }


    fun setProfilePic(url: Uri) {
        this.profilePic = url
    }


    fun getProfilePic(): Uri {
        return this.profilePic
    }

    fun getUserName(): String {
        return this.username
    }

    fun getEmail(): String {
        return this.email
    }

    fun getPhoneNumber(): String {
        return this.phone
    }

    fun getNumTracking(): Int {
        return this.numTracking
    }

    fun setNumTracking(numT: Int) {
        this.numTracking = numT
    }

    fun isLoggedIn(): Boolean {
        if (token != "") {
            return true
        }
        return false
    }

    /**
     * Take and return the JWT token from Firebase
     * Populate displayName, email and photoUrl with server data
     * @param callback callback function for returning JWT
     */
    private fun getToken(callback: (String) -> Unit) {
        val user = firebase.currentUser
        if (user != null) {
            user.getIdToken(false).addOnCompleteListener {
                if (it.isSuccessful) {
                    user.displayName?.let { it1 -> setUsername(it1) }
                    user.email?.let { it1 -> setEmail(it1) }
                    user.photoUrl?.let { it1 -> setPic(it1) }
                    callback(it.result?.token!!)
                } else {
                    loading.postValue(true)
                }
            }
        } else {
            loading.postValue(true)
        }
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
        firebase.currentUser!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    this.username = username
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

        firebase.currentUser!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _pictureChangeRes.postValue(
                        FirebaseOperationResponse(
                            true,
                            FirebaseOperation.UPDATEPIC,
                            ""
                        )
                    )
                    setProfilePic(pic)
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
        password: String,
        _emailChangeRes: MutableLiveData<FirebaseOperationResponse>
    ) {

        val credential: AuthCredential = EmailAuthProvider.getCredential(this.email, password)


        firebase.currentUser!!.reauthenticate(credential).addOnCompleteListener() {

            if (it.isSuccessful) {
                firebase.currentUser!!.updateEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            this.email = email
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

        val credential: AuthCredential = EmailAuthProvider.getCredential(this.email, oldpass)

        firebase.currentUser!!.reauthenticate(credential).addOnCompleteListener() {

            if (it.isSuccessful) {
                firebase.currentUser!!.updatePassword(newpass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful)
                            _passwordChangeRes.postValue(
                                FirebaseOperationResponse(
                                    true,
                                    FirebaseOperation.UPDATEPASSWORD,
                                    ""
                                )
                            ) else _passwordChangeRes.postValue(
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
        firebase.sendPasswordResetEmail(email)
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

    fun delete(
        password: String,
        _deleteRes: MutableLiveData<FirebaseOperationResponse>
    ) {
        val credential: AuthCredential = EmailAuthProvider.getCredential(this.email, password)

        firebase.currentUser!!.reauthenticate(credential).addOnCompleteListener() {

            if (it.isSuccessful) {
                firebase.currentUser!!.delete()
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


    fun flush() {
        token = ""
    }

}