package com.example.track4deals.ui.offers


import androidx.lifecycle.*
import com.example.track4deals.data.database.entity.ProductEntity
import com.example.track4deals.data.repository.ProductRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class OffersViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val addTrack = MutableLiveData<ProductEntity>()
    private val remTrack = MutableLiveData<ProductEntity>()

    private var _offersRes = MutableLiveData<List<ProductEntity>>()
    val offersRes: LiveData<List<ProductEntity>> = _offersRes

    private var _trackingRes = MutableLiveData<List<ProductEntity>>()
    val trackingRes: LiveData<List<ProductEntity>> = _trackingRes


    fun getOffers() {
        viewModelScope.launch {
            productRepository.getOffers().collect {
                _offersRes.postValue(it)
            }
        }
    }

    fun getTrackings(){
        viewModelScope.launch {
            productRepository.getTrackingProducts().collect {
                _trackingRes.postValue(it)
            }
        }
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
