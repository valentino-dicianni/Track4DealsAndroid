package com.example.track4deals.services

import com.example.track4deals.data.constants.AppConstants
import com.example.track4deals.data.models.UserInfo
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


interface OffersService {

    @FormUrlEncoded
    @POST("/offers/get_offers")
    suspend fun getAllOffers(): retrofit2.Response<UserInfo>

    object OffersServiceCreator {
        fun newService(): OffersService {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val httpBuilder = OkHttpClient.Builder()
            httpBuilder.addInterceptor(interceptor)
            httpBuilder.addInterceptor(Interceptor { chain ->
                val r = chain.request()
                val builder = r.newBuilder()
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
            return retrofit.create(OffersService::class.java)
        }
    }
}