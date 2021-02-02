package com.example.track4deals.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.track4deals.data.models.ServerResponseUser
import com.example.track4deals.data.models.UserInfo
import com.example.track4deals.services.UserDataService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(
    private val userDataService: UserDataService
) {

    private val _userResponse = MutableLiveData<ServerResponseUser>()
    val userResponse: LiveData<ServerResponseUser>
        get() = _userResponse


    suspend fun getUser(): LiveData<ServerResponseUser> {
        return withContext(Dispatchers.IO) {
            userDataService.getUser()
            return@withContext userDataService.downloadedUser
        }
    }

    suspend fun modifyUser(user: UserInfo): LiveData<ServerResponseUser> {
        return withContext(Dispatchers.IO) {
            userDataService.modifyUser(user)
            return@withContext userDataService.downloadedUser
        }
    }

}