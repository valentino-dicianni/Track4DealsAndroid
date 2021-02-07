package com.example.track4deals.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.track4deals.data.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    fun resetTracking() {
        viewModelScope.launch(Dispatchers.IO) {
            productRepository.resetTracking()
        }
    }


}