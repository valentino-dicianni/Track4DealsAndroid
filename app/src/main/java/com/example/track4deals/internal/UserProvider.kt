package com.example.track4deals.internal

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest

class UserProvider {
    private var token: String = ""
    private var username: String = ""
    private var email: String = ""
    private var profilePic : Uri = Uri.EMPTY
    private lateinit var caregoty_list:  Array<String?>
    private var phone : String = ""
    private var numTracking : Int = 0
    private var loading = MutableLiveData<Boolean>()

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

    fun setUsername(username: String){
        this.username = username
    }
    fun setProfilePic(url: Uri){
        this.profilePic = url
    }
    fun getProfilePic(): Uri{
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

    fun getNumTracking() : Int {
        return this.numTracking
    }

    fun setNumTracking(numT : Int) {
        this.numTracking = numT
    }

    fun isLoggedIn(): Boolean {
        if (token != "") {
            return true
        }
        return false
    }

    private fun setUserName(username: String) {
        this.username = username
    }

    private fun setEmail(psw: String) {
        this.email = psw
    }

    private fun setPic(pic: Uri) {
        this.profilePic = pic
    }


    private fun getToken(callback: (String) -> Unit) {
        FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnCompleteListener {
            if (it.isSuccessful) {
                FirebaseAuth.getInstance().currentUser?.displayName?.let { it1 -> setUserName(it1) }
                FirebaseAuth.getInstance().currentUser?.email?.let { it1 -> setEmail(it1) }
                FirebaseAuth.getInstance().currentUser?.photoUrl?.let { it1 -> setPic(it1) }
                callback(it.result?.token!!)
            }
        }
    }

    private fun updateUsername(callback: (Boolean) -> Unit, username:String) {
        val profileUpdates = userProfileChangeRequest {
            displayName = username
        }

        FirebaseAuth.getInstance().currentUser!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) callback(true) else callback(false)
                }
    }


    private fun updatePicture(callback: (Boolean) -> Unit, pic:Uri) {
        val profileUpdates = userProfileChangeRequest {
            photoUri = pic
        }

        FirebaseAuth.getInstance().currentUser!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) callback(true) else callback(false)
                }
    }


    private fun updateEmail(callback: (Boolean) -> Unit, email:String) {
        FirebaseAuth.getInstance().currentUser!!.updateEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (task.isSuccessful) callback(true) else callback(false)
                    }
                }
    }


    private fun updatePassword(callback: (Boolean) -> Unit, pass:String) {
        FirebaseAuth.getInstance().currentUser!!.updatePassword(pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (task.isSuccessful) callback(true) else callback(false)
                    }
                }
    }


    private fun resetPassword(callback: (Boolean) -> Unit, email:String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (task.isSuccessful) callback(true) else callback(false)
                    }
                }
    }


    private fun delete(callback: (Boolean) -> Unit) {
        FirebaseAuth.getInstance().currentUser!!.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (task.isSuccessful) callback(true) else callback(false)
                    }
                }
    }


    fun flush() {
        token = ""
    }

}