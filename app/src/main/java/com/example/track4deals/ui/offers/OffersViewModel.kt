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

    suspend fun addTracking(productEntity: ProductEntity) {
       productRepository.addTrackingProduct(productEntity)
    }


    fun removeTracking(productEntity: ProductEntity) {

    }
}