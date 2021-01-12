package com.example.track4deals.ui.offers

import androidx.lifecycle.ViewModel
import com.example.track4deals.data.ProductRepository
import com.example.track4deals.data.database.entity.ProductEntity
import com.example.track4deals.internal.lazyDeferred
import kotlinx.coroutines.delay

class OffersViewModel(
    private val offerRepository: ProductRepository
) : ViewModel() {

    val offers by lazyDeferred {
        offerRepository.getOffers()
    }


    suspend fun addTracking(productEntity: ProductEntity) {
        offerRepository.addTrackingProduct(productEntity)
        delay(5000)
    }
}