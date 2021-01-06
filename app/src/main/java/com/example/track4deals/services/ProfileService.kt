package com.example.track4deals.services

import com.example.track4deals.data.constants.AppConstants
import com.example.track4deals.data.models.UserInfo
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface ProfileService {

    @FormUrlEncoded
    @POST("/profile/modify_profile")
    suspend fun updateProfile(
        @Field("profilePhoto") profilePhoto: String,
        @Field("caregoty_list") caregoty_list:  Array<String?>,
    ): retrofit2.Response<UserInfo>

    object ProfileServiceCreator {
        fun newService(): ProfileService {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val httpBuilder = OkHttpClient.Builder()
            httpBuilder.addInterceptor(interceptor)
            httpBuilder.addInterceptor(Interceptor { chain ->
                val r = chain.request()
                val builder = r.newBuilder()
                //TODO: aggiungere autenticazione
                builder.addHeader("Accept", "application/json")
                builder.addHeader("Content-Type", "application/x-www-form-urlencoded")
                builder.method(r.method, r.body)
                chain.proceed(builder.build())
            })
            httpBuilder.connectTimeout(30, TimeUnit.SECONDS)
            httpBuilder.readTimeout(30, TimeUnit.SECONDS)

            val retrofit = Retrofit.Builder()
                .baseUrl(AppConstants.baseServerURL)
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .client(httpBuilder.build())
                .build()
            return retrofit.create(ProfileService::class.java)
        }
    }
}