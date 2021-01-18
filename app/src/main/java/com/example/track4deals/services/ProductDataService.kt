package com.example.track4deals.services

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.track4deals.data.database.entity.ProductEntity
import com.example.track4deals.data.models.ServerResponse
import com.example.track4deals.internal.NoConnectivityException
import com.google.firebase.auth.FirebaseAuth


class ProductDataService(
    private val offersService: OffersService
) {
    private val _downloadedOffers = MutableLiveData<ServerResponse>()
    val downloadedOffers: LiveData<ServerResponse>
        get() = _downloadedOffers

    private val _downloadeTracking = MutableLiveData<ServerResponse>()
    val downloadeTracking: LiveData<ServerResponse>
        get() = _downloadeTracking

    private val _addTrackingRes = MutableLiveData<ServerResponse>()
    val addTrackingRes: LiveData<ServerResponse>
        get() = _addTrackingRes

    suspend fun getOffers() {
        try {
            val offers = offersService.getAllOffersAsync().await()
            _downloadedOffers.postValue(offers)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", "NO internet connection", e)
        }
    }

    suspend fun getTracking() {
        try {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            var firebaseToken: String = ""
            if (firebaseUser != null) {
                firebaseToken = firebaseUser.getIdToken(false).result?.token!!
            }

            val offers = offersService.getAllTrackingAsync("Bearer $firebaseToken").await()
            _downloadeTracking.postValue(offers)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", "NO internet connection", e)
        }
    }

    suspend fun addTrackProduct(p: ProductEntity) {
        try {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            var firebaseToken: String = ""
            if (firebaseUser != null) {
                firebaseToken = firebaseUser.getIdToken(false).result?.token!!
            }
            var isDeal:Boolean = false
            if(p.isDeal == 1) isDeal = true
            val serverRes = offersService.addTrackingProductAsync(
                firebaseToken,
                p.ASIN,
                p.product_url,
                p.title,
                p.brand,
                p.category,
                p.description,
                p.normal_price,
                p.offer_price,
                p.discount_perc,
                p.imageUrl_large,
                p.imageUrl_medium,
                isDeal,
            ).await()
            _addTrackingRes.postValue(serverRes)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", "NO internet connection", e)
        }
    }


}
