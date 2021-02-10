package com.example.track4deals.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.track4deals.data.repository.ProductRepository
import com.example.track4deals.internal.UserProvider

class SettingsViewModelFactory(
    private val productRepository: ProductRepository,
    private val userProvider: UserProvider
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SettingsViewModel(productRepository, userProvider) as T
    }
}