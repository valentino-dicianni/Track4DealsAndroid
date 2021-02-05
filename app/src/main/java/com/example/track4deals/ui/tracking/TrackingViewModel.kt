package com.example.track4deals.ui.tracking

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.example.track4deals.data.database.entity.ProductEntity
import com.example.track4deals.data.repository.ProductRepository


class TrackingViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val currentProduct =  MutableLiveData<String>()
    private val trackProduct = MutableLiveData<ProductEntity>()

    fun setProduct(p : String) {
        this.currentProduct.postValue(p)
    }

    fun setTrackProduct(p : ProductEntity) {
        this.trackProduct.postValue(p)
    }

    // switchMap starts a coroutine whenever the value of a LiveData changes.
    val verifyProdResult = currentProduct.switchMap {
        liveData {
            productRepository.findProductByAsin(it).value?.let { emit(it) }
        }
    }
    val addTrackingRes = trackProduct.switchMap {
        liveData {
            productRepository.addTrackingProduct(it).value?.let { emit(it) }
        }
    }

}