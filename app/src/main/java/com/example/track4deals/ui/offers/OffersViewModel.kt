package com.example.track4deals.ui.offers


import androidx.lifecycle.ViewModel
import com.example.track4deals.data.ProductRepository
import com.example.track4deals.data.database.entity.ProductEntity
import com.example.track4deals.internal.lazyDeferred
import kotlinx.coroutines.delay

class OffersViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    lateinit var addTrackingProduct : ProductEntity
    lateinit var removeTrackingProduct : ProductEntity

    val offers by lazyDeferred {
        productRepository.getOffers()
    }

    val trackings by lazyDeferred {
        productRepository.getTrackingProducts()
    }

    val addTrackingRes by lazyDeferred {
        productRepository.addTrackingProduct(addTrackingProduct)
    }

    val removeTrackingRes by lazyDeferred {
        productRepository.removeTrackingProduct(removeTrackingProduct)
    }


}