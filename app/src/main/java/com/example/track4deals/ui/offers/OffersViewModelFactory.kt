package com.example.track4deals.ui.offers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.track4deals.data.OffersRepository

class OffersViewModelFactory(
    private val offersRepository: OffersRepository,
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return OffersViewModel(offersRepository) as T
    }
}