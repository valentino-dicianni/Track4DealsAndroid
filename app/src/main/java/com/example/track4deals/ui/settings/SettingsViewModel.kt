package com.example.track4deals.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.provider.Settings.Global.getString
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.track4deals.R
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