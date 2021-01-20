package com.example.track4deals.ui.offers


import androidx.lifecycle.ViewModel
import com.example.track4deals.data.ProductRepository
import com.example.track4deals.data.database.entity.ProductEntity
import com.example.track4deals.internal.lazyDeferred
import kotlinx.coroutines.delay

class OffersViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    val offers by lazyDeferred {
        productRepository.getOffers()
    }

    val trackings by lazyDeferred {
        productRepository.getTrackingProducts()
    }

    suspend fun addTracking(productEntity: ProductEntity) {
       productRepository.addTrackingProduct(productEntity)
    }

    suspend fun removeTracking(productEntity: ProductEntity) {
        productRepository.removeTrackingProduct(productEntity)
    }
}