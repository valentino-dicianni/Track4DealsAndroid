package com.example.track4deals.internal

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.LogPrinter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.preference.PreferenceManager
import com.example.track4deals.data.models.FirebaseOperation
import com.example.track4deals.data.models.FirebaseOperationResponse
import com.example.track4deals.data.models.ServerResponse
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.userProfileChangeRequest
import java.lang.Exception
import java.security.AuthProvider

class UserProvider(
    private val context: Context
) {
    private var token: String = ""
    private var username: String = ""
    private var email: String = ""
    private var profilePic: Uri = Uri.EMPTY
    private lateinit var caregoty_list: Array<String?>
    private var phone: String = ""
    private var numTracking: Int = 0
    private var googleLogin: Boolean = false
    private var googleToken: String = ""
    private var loading = MutableLiveData<Boolean>()
    private var password: String = ""
    private var firebase = FirebaseAuth.getInstance()
    private var sharedPref: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    init {
        getToken {
            token = it
            loading.postValue(true)
        }

        googleLogin = this.sharedPref.getBoolean("isLoggedWithGoogle", false)
        googleToken = this.sharedPref.getString("googleTokenId", "")!!
        password = this.sharedPref.getString("userPass", "")!!
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

    fun setPass(pass: String) {
        this.password = pass
    }

    fun setPic(pic: Uri) {
        this.profilePic = pic
    }


    fun setProfilePic(url: Uri) {
        this.profilePic = url
    }

    fun setGoogleLogin(value: Boolean) {
        this.googleLogin = value
    }

    fun setGoogleToken(token: String) {
        this.googleToken = token
    }

    fun isLoggedWithGoogle(): Boolean {
        return this.googleLogin
    }

    fun getGoogleToken(): String {
        return this.googleToken
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

    fun getPass(): String {
        return this.password
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

    fun flush() {
        token = ""
        profilePic = Uri.EMPTY
        googleLogin = false

        with(sharedPref.edit()) {
            putBoolean("isLoggedWithGoogle", false)
            putString("googleTokenId", "")
            apply()
        }
    }

}