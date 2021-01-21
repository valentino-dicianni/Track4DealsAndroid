package com.example.track4deals.ui.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.track4deals.data.ProductRepository

class TrackingViewModelFactory(
    private val productRepository: ProductRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TrackingViewModel(productRepository) as T
    }
}
