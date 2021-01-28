package com.example.track4deals.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.track4deals.data.repository.LoginRepository
import com.example.track4deals.data.repository.UserRepository
import com.example.track4deals.internal.UserProvider

class ProfileViewModelFactory(
        private val userRepository: UserRepository,
        private val loginRepository: LoginRepository,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(userRepository,loginRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}