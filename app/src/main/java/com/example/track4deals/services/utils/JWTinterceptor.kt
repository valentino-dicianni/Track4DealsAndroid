package com.example.track4deals.services.utils

import com.example.track4deals.internal.UserProvider
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class JWTinterceptor(
    private val userProvider: UserProvider
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = userProvider.getToken()
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