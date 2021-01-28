package com.example.track4deals.internal

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.example.track4deals.data.models.FirebaseOperation
import com.example.track4deals.data.models.FirebaseOperationResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest

class UserProvider {
    private var token: String = ""
    private var username: String = ""
    private var email: String = ""
    private var profilePic: Uri = Uri.EMPTY
    private lateinit var caregoty_list: Array<String?>
    private var phone: String = ""
    private var numTracking: Int = 0
    private var loading = MutableLiveData<Boolean>()
    private var _firebaseResponse = MutableLiveData<FirebaseOperationResponse>()
    val firebaseRespone : LiveData<FirebaseOperationResponse> =_firebaseResponse

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
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            user.getIdToken(false).addOnCompleteListener {
                if (it.isSuccessful) {
                    user.displayName?.let { it1 ->setUserName(it1) }
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

     fun updateUsername( username: String) {
        val profileUpdates = userProfileChangeRequest {
            displayName = username
        }


        FirebaseAuth.getInstance().currentUser!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) _firebaseResponse.postValue(FirebaseOperationResponse(true, FirebaseOperation.UPDATEUSERNAME)) else _firebaseResponse.postValue(FirebaseOperationResponse(true, FirebaseOperation.UPDATEUSERNAME))
            }


    }


     fun updatePicture( pic: Uri) {
        val profileUpdates = userProfileChangeRequest {
            photoUri = pic
        }

        FirebaseAuth.getInstance().currentUser!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) _firebaseResponse.postValue(FirebaseOperationResponse(true, FirebaseOperation.UPDATEPIC)) else _firebaseResponse.postValue(FirebaseOperationResponse(true, FirebaseOperation.UPDATEPIC))
            }
    }


     fun updateEmail( email: String) {
        FirebaseAuth.getInstance().currentUser!!.updateEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.isSuccessful) _firebaseResponse.postValue(FirebaseOperationResponse(true, FirebaseOperation.UPDATEEMAIL)) else _firebaseResponse.postValue(FirebaseOperationResponse(true, FirebaseOperation.UPDATEEMAIL))

                }
            }
    }


     fun updatePassword(pass: String) {
        FirebaseAuth.getInstance().currentUser!!.updatePassword(pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.isSuccessful) _firebaseResponse.postValue(FirebaseOperationResponse(true, FirebaseOperation.UPDATEPASSWORD)) else _firebaseResponse.postValue(FirebaseOperationResponse(true, FirebaseOperation.UPDATEPASSWORD))

                }
            }
    }


     fun resetPassword( email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.isSuccessful) _firebaseResponse.postValue(FirebaseOperationResponse(true, FirebaseOperation.RESETPASSWORD)) else _firebaseResponse.postValue(FirebaseOperationResponse(true, FirebaseOperation.RESETPASSWORD))

                }
            }
    }


     fun delete() {
        FirebaseAuth.getInstance().currentUser!!.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.isSuccessful) _firebaseResponse.postValue(FirebaseOperationResponse(true, FirebaseOperation.DELETE)) else _firebaseResponse.postValue(FirebaseOperationResponse(true, FirebaseOperation.DELETE))

                }
            }
    }


    fun flush() {
        token = ""
    }

}