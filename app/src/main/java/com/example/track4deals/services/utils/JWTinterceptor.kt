package com.example.track4deals.services.utils

import android.util.Log
import com.example.track4deals.internal.TokenProvider
import com.example.track4deals.ui.login.LoginFragment.Companion.TAG
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class JWTinterceptor(
    private val tokenProvider: TokenProvider
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenProvider.get()
        val newRequest: Request
        newRequest = if (token != "") {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(newRequest)
    }
}