package com.example.track4deals.services.utils

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.track4deals.data.models.ServerResponseUser
import com.example.track4deals.internal.NoConnectivityException
import com.example.track4deals.services.ProfileService
import java.net.SocketTimeoutException

class UserDataService (
    private val profileService: ProfileService
    ){

    private val _downloadedUser = MutableLiveData<ServerResponseUser>()
    val downloadedUser : LiveData<ServerResponseUser>
        get() = _downloadedUser


    suspend fun getUser() {
        try {
            val user = profileService.getUserAsync().await()
            _downloadedUser.postValue(user)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", "NO internet connection", e)
        } catch (e: SocketTimeoutException) {
            Log.e("Connectivity", "TimeOut exception", e)
        }
    }
}