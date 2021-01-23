package com.example.track4deals.data


import android.util.Log
import androidx.lifecycle.LiveData
import com.example.track4deals.data.database.ProductDAO
import com.example.track4deals.data.database.entity.ProductEntity
import com.example.track4deals.data.models.Product
import com.example.track4deals.data.models.ServerResponse
import com.example.track4deals.services.ProductDataService
import kotlinx.coroutines.*
import org.threeten.bp.ZonedDateTime

class ProductRepository(
    private val productDAO: ProductDAO,
    private val productDataService: ProductDataService
) {
    private var lastFetchTimeOffers: ZonedDateTime = ZonedDateTime.now().minusMinutes(30)
    private var lastFetchTimeTrackings: ZonedDateTime = ZonedDateTime.now().minusMinutes(30)

    init {
        productDataService.downloadedOffers.observeForever { newOffers ->
            persistProductData(newOffers, true, 0)
        }
        productDataService.downloadeTracking.observeForever { newTrackings ->
            persistProductData(newTrackings, false, 1)
        }
        productDataService.addTrackingRes.observeForever { trackingResponse ->
            persistProductData(trackingResponse, false, 1)
        }
        productDataService.removeTrackingRes.observeForever { trackingResponse ->
            persistProductData(trackingResponse, false, 0)
        }
    }

    private fun persistProductData(
        newOffers: ServerResponse?,
        customUpsert: Boolean,
        isTracking: Int
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val products: ArrayList<Product>? = newOffers?.response
            if (products != null) {
                for (p: Product in products) {
                    if (customUpsert)
                        productDAO.customUpsert(p.productToEntity(isTracking))
                    else
                        productDAO.upsert(p.productToEntity(isTracking))
                }
            }
        }
    }


    suspend fun getOffers(): LiveData<List<ProductEntity>> {
        return withContext(Dispatchers.IO) {
            if (isServerFetchtNeeded(lastFetchTimeOffers)) {
                productDataService.getOffers()
                lastFetchTimeOffers = ZonedDateTime.now()
                Log.d("TEST", "getOffers: update dal server: $lastFetchTimeOffers")
            }
            return@withContext productDAO.getAllProduct()
        }
    }

    suspend fun getTrackingProducts(): LiveData<List<ProductEntity>> {
        return withContext(Dispatchers.IO) {
            if (isServerFetchtNeeded(lastFetchTimeOffers)) {
            productDataService.getTracking()
                lastFetchTimeTrackings = ZonedDateTime.now()
                Log.d("TEST", "getOffers: update dal server: $lastFetchTimeTrackings")
            }
            return@withContext productDAO.getAllTracking()
        }
    }


    suspend fun addTrackingProduct(productEntity: ProductEntity): LiveData<ServerResponse> {
        return withContext(Dispatchers.IO) {
            productDataService.addTrackProduct(productEntity)
        }
    }

    suspend fun removeTrackingProduct(productEntity: ProductEntity): LiveData<ServerResponse> {
        return withContext(Dispatchers.IO) {
            productDataService.removeTrackProduct(productEntity)
        }
    }

    suspend fun findProductByAsin(ASIN: String): LiveData<ServerResponse> {
        return withContext(Dispatchers.IO) {
            productDataService.fetchAmazonProduct(ASIN)
        }
    }

    private fun isServerFetchtNeeded(lastTime: ZonedDateTime): Boolean {
        val oneMinutesAgo = ZonedDateTime.now().minusMinutes(1)
        return lastTime.isBefore(oneMinutesAgo)
    }
}