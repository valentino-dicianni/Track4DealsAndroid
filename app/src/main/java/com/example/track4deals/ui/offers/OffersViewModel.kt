package com.example.track4deals.ui.offers


import androidx.lifecycle.*
import com.example.track4deals.data.database.entity.ProductEntity
import com.example.track4deals.data.models.LoginFormState
import com.example.track4deals.data.models.Product
import com.example.track4deals.data.models.ServerResponse
import com.example.track4deals.data.repository.ProductRepository
import com.example.track4deals.internal.lazyDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OffersViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val addTrack = MutableLiveData<ProductEntity>()
    private val remTrack = MutableLiveData<ProductEntity>()

    // LAZY!! chiamate solo all'occorrenza per la recycler view
    val offers by lazyDeferred {
        productRepository.getOffers()
    }

    val trackings by lazyDeferred {
        productRepository.getTrackingProducts()
    }

    fun setAddT(p: ProductEntity) {
        this.addTrack.postValue(p)
    }

    fun setRemT(p: ProductEntity) {
        this.remTrack.postValue(p)
    }

    // switchMap starts a coroutine whenever the value of a LiveData changes.
    val addTrackingRes = addTrack.switchMap {
        liveData {
            productRepository.addTrackingProduct(it).value?.let { emit(it) }
        }
    }

    val removeTrackingRes = remTrack.switchMap {
        liveData {
            productRepository.removeTrackingProduct(it).value?.let { emit(it) }
        }
    }


}
