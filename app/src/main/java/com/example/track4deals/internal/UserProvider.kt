package com.example.track4deals.internal

import com.google.firebase.auth.FirebaseAuth

class UserProvider {
    private var token: String = ""
    private var username: String = ""
    private var email: String = ""
    private var profilePic : String = ""
    private var phone : String = ""
    private var numTracking : Int = 0

    init {
        getToken {
            token = it
        }
    }

    fun getToken() = token

    fun loadToken() {
        getToken {
            token = it
        }
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


    private fun getToken(callback: (String) -> Unit) {
        FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnCompleteListener {
            if (it.isSuccessful) {
                FirebaseAuth.getInstance().currentUser?.displayName?.let { it1 -> setUserName(it1) }
                FirebaseAuth.getInstance().currentUser?.email?.let { it1 -> setEmail(it1) }
                callback(it.result?.token!!)
            }
        }
    }

    fun flush() {
        token = ""
    }

}