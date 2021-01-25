package com.example.track4deals.services

import com.example.track4deals.data.constants.AppConstants
import com.example.track4deals.data.models.ServerResponse
import com.example.track4deals.data.models.ServerResponseUser
import com.example.track4deals.data.models.UserInfo
import com.example.track4deals.services.utils.ConnectivityInterceptor
import com.example.track4deals.services.utils.JWTinterceptor
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface ProfileService {

    @POST("/profile/get_user")
    fun getUserAsync(): Deferred<ServerResponseUser>

    @FormUrlEncoded
    @POST("/profile/modify_profile")
    suspend fun updateProfile(
        @Field("profilePhoto") profilePhoto: String,
        @Field("caregoty_list") caregoty_list:  Array<String?>,
    ): Deferred<ServerResponseUser>


    companion object {
        operator fun invoke(
                connectivityInterceptor: ConnectivityInterceptor,
                jwTinterceptor: JWTinterceptor
        ): ProfileService {
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
                    .addInterceptor(jwTinterceptor)
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
                    .create(ProfileService::class.java)
        }
    }
}