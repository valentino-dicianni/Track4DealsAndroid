package com.example.track4deals.services

import com.example.track4deals.data.constants.AppConstants
import com.example.track4deals.data.models.ServerResponse
import com.example.track4deals.data.models.UserInfo
import com.example.track4deals.services.utils.ConnectivityInterceptor
import com.example.track4deals.services.utils.JWTinterceptor
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.time.Duration
import java.util.concurrent.TimeUnit


interface OffersService {

    @GET("/offers/get_offers")
    fun getAllOffersAsync(): Deferred<ServerResponse>

    @GET("/tracking/get_offers")
    fun getAllTrackingAsync(): Deferred<ServerResponse>


    @FormUrlEncoded
    @POST("/tracking/verify")
    fun verifyProductAsync(@Field("productASIN") asin: String): Deferred<ServerResponse>

    @FormUrlEncoded
    @POST("/tracking/add_tracking")
    fun addTrackingProductAsync(
        @Field("ASIN") ASIN: String,
        @Field("product_url") product_url: String,
        @Field("title") title: String,
        @Field("brand") brand: String,
        @Field("category") category: String,
        @Field("description") description: String,
        @Field("normal_price") normal_price: Double,
        @Field("offer_price") offer_price: Double,
        @Field("discount_perc") discount_perc: Double,
        @Field("imageUrl_large") imageUrl_large: String,
        @Field("imageUrl_medium") imageUrl_medium: String,
        @Field("isDeal") isDeal: Boolean
    ): Deferred<ServerResponse>


    @FormUrlEncoded
    @POST("/tracking/remove_tracking")
    fun removeTrackingProductAsync(
        @Field("ASIN") ASIN: String,
        @Field("product_url") product_url: String,
        @Field("title") title: String,
        @Field("brand") brand: String,
        @Field("category") category: String,
        @Field("description") description: String,
        @Field("normal_price") normal_price: Double,
        @Field("offer_price") offer_price: Double,
        @Field("discount_perc") discount_perc: Double,
        @Field("imageUrl_large") imageUrl_large: String,
        @Field("imageUrl_medium") imageUrl_medium: String,
        @Field("isDeal") isDeal: Boolean
    ): Deferred<ServerResponse>


    companion object {
        operator fun invoke(
            connectivityInterceptor: ConnectivityInterceptor,
            jwTinterceptor: JWTinterceptor
        ): OffersService {
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
                .create(OffersService::class.java)
        }
    }
}