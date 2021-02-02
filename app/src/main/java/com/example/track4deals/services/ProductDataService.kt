package com.example.track4deals.services

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.track4deals.data.database.entity.ProductEntity
import com.example.track4deals.data.models.ServerResponse
import com.example.track4deals.services.utils.NoConnectivityException
import com.example.track4deals.services.utils.NoInternetException
import retrofit2.HttpException
import java.net.SocketTimeoutException


class ProductDataService(
    private val offersService: OffersService
) {
    private val _downloadedOffers = MutableLiveData<ServerResponse>()
    val downloadedOffers: LiveData<ServerResponse>
        get() = _downloadedOffers

    private val _downloadeTracking = MutableLiveData<ServerResponse>()
    val downloadedTracking: LiveData<ServerResponse>
        get() = _downloadeTracking

    private val _addTrackingRes = MutableLiveData<ServerResponse>()
    val addTrackingRes: LiveData<ServerResponse>
        get() = _addTrackingRes

    private val _removeTrackingRes = MutableLiveData<ServerResponse>()
    val removeTrackingRes: LiveData<ServerResponse>
        get() = _removeTrackingRes

    private val _fetchedProduct = MutableLiveData<ServerResponse>()
    val fetchedProduct: LiveData<ServerResponse>
        get() = _fetchedProduct


    suspend fun getOffers() {
        try {
            val offers = offersService.getAllOffersAsync().await()
            _downloadedOffers.postValue(offers)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", e.message)
        } catch (e: NoInternetException) {
            Log.e("Connectivity", e.message)
        } catch (e: SocketTimeoutException) {
            Log.e("Connectivity", "TimeOut exception", e)
        } catch (e: HttpException) {
            Log.e("Http", "Exception", e)
        }
    }

    suspend fun getTracking() {
        try {
            val offers = offersService.getAllTrackingAsync().await()
            _downloadeTracking.postValue(offers)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", e.message)
        } catch (e: NoInternetException) {
            Log.e("Connectivity", e.message)
        } catch (e: SocketTimeoutException) {
            Log.e("Connectivity", "TimeOut exception", e)
        } catch (e: HttpException) {
            Log.e("Http", "Exception", e)
        }
    }

    suspend fun fetchAmazonProduct(ASIN: String): LiveData<ServerResponse> {
        try {
            val res = offersService.verifyProductAsync(ASIN).await()
            _fetchedProduct.postValue(res)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", e.message)
        } catch (e: NoInternetException) {
            Log.e("Connectivity", e.message)
        } catch (e: SocketTimeoutException) {
            Log.e("Connectivity", "TimeOut exception", e)
        } catch (e: HttpException) {
            Log.e("Http", "Exception", e)
        }
        return fetchedProduct
    }

    suspend fun addTrackProduct(p: ProductEntity): LiveData<ServerResponse> {
        try {
            var isDeal = false
            if (p.isDeal == 1) isDeal = true
            val serverRes = offersService.addTrackingProductAsync(
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
            Log.e("Connectivity", e.message)
        } catch (e: NoInternetException) {
            Log.e("Connectivity", e.message)
        } catch (e: SocketTimeoutException) {
            Log.e("Connectivity", "TimeOut exception", e)
        } catch (e: HttpException) {
            Log.e("Http", "Exception", e)
        }
        return addTrackingRes
    }

    suspend fun removeTrackProduct(p: ProductEntity): LiveData<ServerResponse> {
        try {
            var isDeal = false
            if (p.isDeal == 1) isDeal = true
            val res = offersService.removeTrackingProductAsync(
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
            _removeTrackingRes.postValue(res)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", e.message)
        } catch (e: NoInternetException) {
            Log.e("Connectivity", e.message)
        } catch (e: SocketTimeoutException) {
            Log.e("Connectivity", "TimeOut exception", e)
        } catch (e: HttpException) {
            Log.e("Http", "Exception", e)
        }
        return removeTrackingRes
    }
}
