package com.example.track4deals.services.utils

import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class JWTinterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        var firebaseToken: String = ""
        if (firebaseUser != null) {
            firebaseToken = firebaseUser.getIdToken(false).result?.token!!
        }
        val newRequest: Request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $firebaseToken")
            .build()
        return chain.proceed(newRequest)
    }
}