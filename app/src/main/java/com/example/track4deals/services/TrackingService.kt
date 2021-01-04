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

interface TrackingService {

    @FormUrlEncoded
    @POST("/tracking/get_offers")
    suspend fun getTrackingOffers(): retrofit2.Response<UserInfo>

    @FormUrlEncoded
    // TODO: controllare il parametro productID sia passato corretamente
    @POST("/tracking/verify/:productID")
    suspend fun verifyProduct(
        @Field("profilePhoto") profilePhoto: String,
        @Field("caregoty_list") caregoty_list:  Array<String?>,
    ): retrofit2.Response<UserInfo>

    @FormUrlEncoded
    @POST("/tracking/add_tracking")
    suspend fun addTrackingProduct(
        @Field("ASIN") ASIN: String,
        @Field("product_url") product_url: String,
        @Field("title") title:  String,
        @Field("brand") brand:  String,
        @Field("category") category:  String,
        @Field("description") description:  String,
        @Field("normal_price") normal_price:  Number,
        @Field("offer_price") offer_price:  Number,
        @Field("discount_perc") discount_perc:  Number,
        @Field("imageUrl_large") imageUrl_large:  String,
        @Field("imageUrl_medium") imageUrl_medium:  String,
        @Field("isDeal") isDeal:  Boolean
    ): retrofit2.Response<UserInfo>


    @FormUrlEncoded
    @POST("/tracking/enable_notifications")
    suspend fun enableTrackingNotifications(
        @Field("firebaseToken") firebaseToken: String,
    ): retrofit2.Response<UserInfo>

    object TrackingServiceCreator {
        fun newService(): TrackingService {
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
            return retrofit.create(TrackingService::class.java)
        }
    }
}