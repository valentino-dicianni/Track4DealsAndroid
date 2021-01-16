package com.example.track4deals.services

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.track4deals.data.models.ServerResponse
import com.example.track4deals.internal.NoConnectivityException


class ProductDataService(
    private val offersService: OffersService
) {
    private val _downloadedOffers = MutableLiveData<ServerResponse>()
    val downloadedOffers: LiveData<ServerResponse>
        get() = _downloadedOffers

    private val _downloadeTracking = MutableLiveData<ServerResponse>()
    val downloadeTracking: LiveData<ServerResponse>
        get() = _downloadeTracking

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
            val offers = offersService.getAllTrackingAsync().await()
            _downloadeTracking.postValue(offers)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", "NO internet connection", e)
        }
    }


}
