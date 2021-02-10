package com.example.track4deals.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.track4deals.data.repository.ProductRepository
import com.example.track4deals.internal.UserProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val productRepository: ProductRepository,
    private val userProvider: UserProvider
) : ViewModel() {


    fun logout() {
        FirebaseAuth.getInstance().signOut()
        userProvider.flush()
        resetTracking()
    }

    private fun resetTracking() {
        viewModelScope.launch(Dispatchers.IO) {
            productRepository.resetTracking()
        }
    }
}