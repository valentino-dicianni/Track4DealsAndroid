package com.example.track4deals.data.repository


import androidx.lifecycle.LiveData
import com.example.track4deals.data.database.ProductDAO
import com.example.track4deals.data.database.entity.ProductEntity
import com.example.track4deals.data.models.Product
import com.example.track4deals.data.models.ServerResponse
import com.example.track4deals.services.ProductDataService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
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
        productDataService.downloadedTracking.observeForever { newTrackings ->
            persistProductData(newTrackings, false, 1)
        }
        productDataService.addTrackingRes.observeForever { trackingResponse ->
            persistProductData(trackingResponse, false, 1)
            lastFetchTimeTrackings = ZonedDateTime.now().minusMinutes(30)
        }
        productDataService.removeTrackingRes.observeForever { trackingResponse ->
            persistProductData(trackingResponse, false, 0)
            lastFetchTimeTrackings = ZonedDateTime.now().minusMinutes(30)
        }
    }

    /**
     * Makes persistent the downloaded data from server.
     * For downloaded offers we need to use a custom Upsert due to
     * the difference between local database and remote database
     *
     * @param customUpsert true if we need to use the custom upser in DAO
     * @param isTracking true if the data to persist needs to tracked
     * @param serverResponse response from server
     *
     */
    private fun persistProductData(
        serverResponse: ServerResponse?,
        customUpsert: Boolean,
        isTracking: Int
    ) {
        runBlocking(Dispatchers.IO) {
            val products: ArrayList<Product>? = serverResponse?.response
            if (products != null) {
                if (customUpsert)
                    productDAO.upsertAllCustom(products, isTracking)
                else
                    productDAO.upsertAll(products, isTracking)
            }
        }
    }

    suspend fun getOffers(): Flow<List<ProductEntity>> {
        return withContext(Dispatchers.IO) {
            if (isServerFetchNeeded(lastFetchTimeOffers)) {
                productDataService.getOffers()
                lastFetchTimeOffers = ZonedDateTime.now()
            }
            return@withContext productDAO.getAllProduct().distinctUntilChanged()
        }
    }

    suspend fun getTrackingProducts(): Flow<List<ProductEntity>> {
        return withContext(Dispatchers.IO) {
            productDataService.getTracking()
            return@withContext productDAO.getAllTracking().distinctUntilChanged()
        }
    }


    suspend fun addTrackingProduct(productEntity: ProductEntity): LiveData<ServerResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext productDataService.addTrackProduct(productEntity)
        }
    }

    suspend fun removeTrackingProduct(productEntity: ProductEntity): LiveData<ServerResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext productDataService.removeTrackProduct(productEntity)
        }
    }

    suspend fun findProductByAsin(ASIN: String): LiveData<ServerResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext productDataService.fetchAmazonProduct(ASIN)
        }
    }

    fun resetTracking() {
        productDAO.resetTracking()
    }

    /**
     * check if refreshed product are needed from the server
     */
    private fun isServerFetchNeeded(lastTime: ZonedDateTime): Boolean {
        val oneMinutesAgo = ZonedDateTime.now().minusMinutes(1)
        return lastTime.isBefore(oneMinutesAgo)
    }
}