package com.example.track4deals.ui.profile

import androidx.lifecycle.*
import com.example.track4deals.R
import com.example.track4deals.data.models.ChangePasswordFormState
import com.example.track4deals.data.models.FirebaseOperationResponse
import com.example.track4deals.data.models.UserInfo
import com.example.track4deals.data.repository.AuthRepository
import com.example.track4deals.data.repository.UserRepository
import com.example.track4deals.internal.lazyDeferred

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val changeForm = MutableLiveData<ChangePasswordFormState>()
    val changeFormState: LiveData<ChangePasswordFormState> = changeForm
    private val modifiedUser = MutableLiveData<UserInfo>()
    private var firebaseResponse = MutableLiveData<FirebaseOperationResponse>()
    private val modifiedEmail = MutableLiveData<String>()
    private val modifiedPic = MutableLiveData<String>()
    private val passwordLive = MutableLiveData<String>()
    private val deleteLive = MutableLiveData<Boolean>()
    private val profileImage = MutableLiveData<String>()


    private val _emailChangeRes = MutableLiveData<FirebaseOperationResponse>()
    private val emailChangeRes: LiveData<FirebaseOperationResponse>
        get() = _emailChangeRes

    private val _deleteRes = MutableLiveData<FirebaseOperationResponse>()
    private val deleteRes: LiveData<FirebaseOperationResponse>
        get() = _deleteRes

    private val _usernameChangeRes = MutableLiveData<FirebaseOperationResponse>()
    val usernameChangeRes: LiveData<FirebaseOperationResponse> = _usernameChangeRes

    private val _passwordChangeRes = MutableLiveData<FirebaseOperationResponse>()
    val passwordChangeRes: LiveData<FirebaseOperationResponse> = _passwordChangeRes


    val changeEmailPairLiveData: LiveData<Pair<String, String>> = object : MediatorLiveData<Pair<String, String>>() {
        var password: String? = null
        var email: String? = null

        init {
            addSource(passwordLive) { passowrd ->
                this.password = passowrd
                email?.let { value = password!! to it }
            }
            addSource(modifiedEmail) { email ->
                this.email = email
                password?.let { value = it to email }
            }
        }
    }
    val deletePairLiveData: LiveData<Pair<String, Boolean>> = object : MediatorLiveData<Pair<String, Boolean>>() {
        var password: String? = null
        var delete: Boolean? = null

        init {
            addSource(passwordLive) { passwrd ->
                this.password = passwrd
                delete?.let { value = password!! to it }
            }
            addSource(deleteLive) { delete ->
                this.delete = delete
                password?.let { value = it to delete }
            }
        }
    }



    val user by lazyDeferred {
        userRepository.getUser()
    }

    fun modifyUser(u: UserInfo) {
        this.modifiedUser.postValue(u)
    }


    fun modifyEmail(e: String) {
        this.modifiedEmail.postValue(e)
    }

    fun delete(d:Boolean) {
        this.deleteLive.postValue(d)
    }


    fun sendPassword(p: String) {
        this.passwordLive.postValue(p)
    }

    fun updateProfilePic(it: String) {
        profileImage.postValue(it)
    }

    fun getProfilePic(): String? {
        return profileImage.value
    }

    // switchMap starts a coroutine whenever the value of a LiveData changes.
    val addUserRes = modifiedUser.switchMap {
        liveData {
            userRepository.modifyUser(it).value?.let { emit(it) }
        }
    }


    fun updateUsername(newUsername:String){
        authRepository.updateUsername(newUsername, _usernameChangeRes)
    }


    val updateEmailResult = changeEmailPairLiveData.switchMap {
        liveData {
            authRepository.updateEmail(it.second, it.first, _emailChangeRes)
            emit(emailChangeRes)
        }
    }


    fun changePassword(oldPass:String, newPass:String){
        authRepository.updatePassword(oldPass,newPass, _passwordChangeRes)
    }


    val deleteResult = deletePairLiveData.switchMap {
        liveData {
            authRepository.delete(it.first,_deleteRes)
            emit(deleteRes)
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