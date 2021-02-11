package com.example.track4deals.internal

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.preference.PreferenceManager
import com.google.firebase.auth.*

class UserProvider(
    private val context: Context
) {
    private var jwtToken: String = ""
    private var username: String = ""
    private var email: String = ""
    private var profilePic: Uri = Uri.EMPTY
    private lateinit var caregoty_list: Array<String?>
    private var phone: String = ""
    private var numTracking: Int = 0
    private var googleLogin: Boolean = false
    private var googleAuthToken: String = ""
    private var loading = MutableLiveData<Boolean>()
    private var password: String = ""
    private var firebase = FirebaseAuth.getInstance()
    private var sharedPref: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    init {
        getJwtToken {
            jwtToken = it
            loading.postValue(true)
        }

        googleLogin = this.sharedPref.getBoolean("isLoggedWithGoogle", false)
        googleAuthToken = this.sharedPref.getString("googleTokenId", "")!!
        password = this.sharedPref.getString("userPass", "")!!
    }

    val loadingComplete = loading.switchMap {
        liveData {
            emit(it)
        }
    }


    fun getJwtToken() = jwtToken

    fun loadJwtToken(token: String) {
        this.jwtToken = token
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

    fun setGoogleAuthToken(token: String) {
        this.googleAuthToken = token
    }

    fun isLoggedWithGoogle(): Boolean {
        return this.googleLogin
    }

    fun getGoogleAuthToken(): String {
        return this.googleAuthToken
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
        if (jwtToken != "") {
            return true
        }
        return false
    }

    /**
     * Take and return the JWT token from Firebase
     * Populate displayName, email and photoUrl with server data
     * @param callback callback function for returning JWT
     */
    private fun getJwtToken(callback: (String) -> Unit) {
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
        jwtToken = ""
        profilePic = Uri.EMPTY
        googleLogin = false

        with(sharedPref.edit()) {
            putBoolean("isLoggedWithGoogle", false)
            putString("googleTokenId", "")
            apply()
        }
    }

}