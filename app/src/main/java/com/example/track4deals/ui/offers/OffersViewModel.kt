package com.example.track4deals.ui.offers

import androidx.lifecycle.ViewModel
import com.example.track4deals.data.OffersRepository
import com.example.track4deals.internal.lazyDeferred

class OffersViewModel(
    private val offerRepository: OffersRepository
) : ViewModel() {

    val offers by lazyDeferred {
        offerRepository.getOffers()
    }
}