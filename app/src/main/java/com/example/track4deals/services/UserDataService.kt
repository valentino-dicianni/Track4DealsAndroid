package com.example.track4deals.services

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.track4deals.data.models.ServerResponseUser
import com.example.track4deals.data.models.UserInfo
import com.example.track4deals.services.utils.NoConnectivityException
import com.example.track4deals.services.utils.NoInternetException
import java.net.SocketTimeoutException

class UserDataService(
    private val profileService: ProfileService
) {

    private val _downloadedUser = MutableLiveData<ServerResponseUser>()
    val downloadedUser: LiveData<ServerResponseUser>
        get() = _downloadedUser


    suspend fun getUser() {
        try {
            val user = profileService.getUserAsync().await()
            _downloadedUser.postValue(user)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", e.message)
        } catch (e: NoInternetException) {
            Log.e("Connectivity", e.message)
        } catch (e: SocketTimeoutException) {
            Log.e("Connectivity", "TimeOut exception", e)
        }
    }


    suspend fun modifyUser(user: UserInfo) {
        try {
            val userRes =
                profileService.updateProfile(user.profilePhoto, user.category_list).await()
            _downloadedUser.postValue(userRes)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", e.message)
        } catch (e: NoInternetException) {
            Log.e("Connectivity", e.message)
        } catch (e: SocketTimeoutException) {
            Log.e("Connectivity", "TimeOut exception", e)
        }
    }
}