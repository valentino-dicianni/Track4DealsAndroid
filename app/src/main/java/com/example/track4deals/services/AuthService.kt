package com.example.track4deals.services

import com.example.track4deals.data.constants.AppConstants
import com.example.track4deals.data.models.ServerResponse
import com.example.track4deals.data.models.UserInfo
import com.example.track4deals.services.utils.ConnectivityInterceptor
import com.example.track4deals.services.utils.JWTinterceptor
import com.google.firebase.inject.Deferred
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


interface AuthService {

    @FormUrlEncoded
    @POST("/auth/creat_account")
    suspend fun registerNewUser(
        @Field("displayName") displayName: String,
        @Field("password") password: String,
        @Field("email") email: String
    ): retrofit2.Response<UserInfo>

    @FormUrlEncoded
    @POST("/auth/create_google_account")
    suspend fun registerNewUserGoogle(
        @Field("displayName") displayName: String,
        @Field("password") password: String,
        @Field("email") email: String,
        @Field("uid") uid: String
    ): retrofit2.Response<UserInfo>

    @FormUrlEncoded
    @POST("/auth/register_firebaseToken")
    suspend fun registerFirebaseToken(
        @Field("firebaseToken") token: String
    ): Deferred<ServerResponse>

    companion object {
        operator fun invoke(
            connectivityInterceptor: ConnectivityInterceptor,
        ): AuthService {
            val requestInterceptor = Interceptor { chain ->
                val url = chain.request().url
                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()
                return@Interceptor chain.proceed(request)
            }

            val okHttpClient = OkHttpClient.Builder()
                .callTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(requestInterceptor)
                .addInterceptor(connectivityInterceptor)
                .addInterceptor(Interceptor { chain ->
                    val r = chain.request()
                    val builder = r.newBuilder()
                    builder.addHeader("Accept", "application/json")
                    builder.addHeader("Content-Type", "application/x-www-form-urlencoded")
                    builder.method(r.method, r.body)
                    chain.proceed(builder.build())
                })
                .build()

            return Retrofit.Builder().client(okHttpClient)
                .baseUrl(AppConstants.baseServerURL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AuthService::class.java)
        }
    }
}