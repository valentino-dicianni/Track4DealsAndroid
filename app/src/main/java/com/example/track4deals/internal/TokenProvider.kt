package com.example.track4deals.internal

import com.google.firebase.auth.FirebaseAuth

class TokenProvider {
    private var token: String = ""

    fun get() = token

    fun load() {
        getToken {
            token = it
        }
    }

    private fun getToken(callback: (String) -> Unit) {
        FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnCompleteListener {
            if (it.isSuccessful) {
                callback(it.result?.token!!)
            }
        }
    }

    fun flush() {
        token = ""
    }
}