package com.example.track4deals.ui.tracking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.track4deals.data.ProductRepository
import com.example.track4deals.internal.lazyDeferred
import kotlinx.coroutines.delay

class TrackingViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    lateinit var currentProduct : String

    fun setProduct(p : String) {
        this.currentProduct = p
    }

    val trackingProduct by lazyDeferred {
        productRepository.findProductByAsin(currentProduct)
    }

    suspend fun trackProduct(link: String) {
        delay(5000)
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }


}