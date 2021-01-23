package com.example.track4deals.ui.tracking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.track4deals.data.ProductRepository
import com.example.track4deals.data.database.entity.ProductEntity
import com.example.track4deals.data.models.Product
import com.example.track4deals.internal.lazyDeferred
import kotlinx.coroutines.delay

class TrackingViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    lateinit var currentProduct : String
    lateinit var trackProduct : ProductEntity

    fun setProduct(p : String) {
        this.currentProduct = p
    }

    val trackingProduct by lazyDeferred {
        productRepository.findProductByAsin(currentProduct)
    }
    val addTrackingRes by lazyDeferred {
        productRepository.addTrackingProduct(trackProduct)
    }

}