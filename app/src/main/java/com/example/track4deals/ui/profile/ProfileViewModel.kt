package com.example.track4deals.ui.profile

import androidx.lifecycle.*
import com.example.track4deals.R
import com.example.track4deals.data.database.entity.ProductEntity
import com.example.track4deals.data.repository.LoginRepository
import com.example.track4deals.data.models.ChangePasswordFormState
import com.example.track4deals.data.models.FirebaseOperationResponse
import com.example.track4deals.data.models.UserInfo
import com.example.track4deals.data.repository.UserRepository
import com.example.track4deals.internal.UserProvider
import com.example.track4deals.internal.lazyDeferred

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val changeForm = MutableLiveData<ChangePasswordFormState>()
    val changeFormState: LiveData<ChangePasswordFormState> = changeForm
    private val modifiedUser = MutableLiveData<UserInfo>()
    private var firebaseResponse = MutableLiveData<FirebaseOperationResponse>()
    private val modifiedUsername = MutableLiveData<String>()
    private val modifiedEmail = MutableLiveData<String>()
    private val modifiedPic = MutableLiveData<String>()
    private val modifiedPass = MutableLiveData<String>()


    val user by lazyDeferred {
        userRepository.getUser()
    }

    fun modifyUser(u: UserInfo) {
        this.modifiedUser.postValue(u)
    }


    fun modifyUsername(u: String) {
        this.modifiedUsername.postValue(u)
    }

    fun modifyEmail(e: String) {
        this.modifiedEmail.postValue(e)
    }

    fun modifyPassword(p: String) {
        this.modifiedPass.postValue(p)
    }


    // switchMap starts a coroutine whenever the value of a LiveData changes.
    val addUserRes = modifiedUser.switchMap {
        liveData {
            userRepository.modifyUser(it).value?.let { emit(it) }
        }
    }


    val updateUsernameRes = modifiedUsername.switchMap {
        liveData {
            emit(loginRepository.updateUsername(it))
        }
    }

    val updateEmailRes = modifiedEmail.switchMap {
        liveData {
            emit(loginRepository.updateEmail(it))
        }
    }

    val updatePassRes = modifiedPass.switchMap {
        liveData {
            emit(loginRepository.updatePassword(it))
        }
    }


    //INPUT:    String value from change password text field
    //OUTPUT:   No returned value.
    //Set the LiveData variable changeForm.value with right ChangePasswordForm state value for displaying the right error in the view
    fun passwdDataChanged(oldPswd: String, newPswd: String, rptPswd: String) {

        if (!isPasswordValid(oldPswd)) {
            changeForm.value = ChangePasswordFormState(oldPasswordError = R.string.invalid_password)
        } else if (!isPasswordValid(newPswd)) {
            changeForm.value = ChangePasswordFormState(newPasswordError = R.string.invalid_password)
        } else if (!isPasswordValid(rptPswd)) {
            changeForm.value =
                ChangePasswordFormState(repeatPasswordError = R.string.invalid_password)
        } else if (newPswd != rptPswd) {
            changeForm.value =
                ChangePasswordFormState(repeatPasswordError = R.string.pswd_must_be_equal)
        } else {
            changeForm.value = ChangePasswordFormState(isDataValid = true)
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

}