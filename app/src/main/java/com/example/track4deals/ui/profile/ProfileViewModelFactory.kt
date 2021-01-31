package com.example.track4deals.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.track4deals.data.repository.AuthRepository
import com.example.track4deals.data.repository.UserRepository

class ProfileViewModelFactory(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(userRepository,authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}