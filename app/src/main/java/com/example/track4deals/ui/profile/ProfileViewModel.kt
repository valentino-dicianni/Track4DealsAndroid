package com.example.track4deals.ui.profile

import android.net.Uri
import androidx.lifecycle.*
import com.example.track4deals.R
import com.example.track4deals.data.models.ChangePasswordFormState
import com.example.track4deals.data.models.FirebaseOperationResponse
import com.example.track4deals.data.models.UserInfo
import com.example.track4deals.data.repository.AuthRepository
import com.example.track4deals.data.repository.UserRepository

class ProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val changeForm = MutableLiveData<ChangePasswordFormState>()
    val changeFormState: LiveData<ChangePasswordFormState> = changeForm

    private val modifiedEmail = MutableLiveData<String>()

    private val _emailChangeRes = MutableLiveData<FirebaseOperationResponse>()
    val emailChangeRes: LiveData<FirebaseOperationResponse> = _emailChangeRes


    private val _deleteRes = MutableLiveData<FirebaseOperationResponse>()
    val deleteRes: LiveData<FirebaseOperationResponse> = _deleteRes

    private val _usernameChangeRes = MutableLiveData<FirebaseOperationResponse>()
    val usernameChangeRes: LiveData<FirebaseOperationResponse> = _usernameChangeRes

    private val _passwordChangeRes = MutableLiveData<FirebaseOperationResponse>()
    val passwordChangeRes: LiveData<FirebaseOperationResponse> = _passwordChangeRes

    private val _pictureChangeRes = MutableLiveData<FirebaseOperationResponse>()
    val pictureChangeRes: LiveData<FirebaseOperationResponse> = _pictureChangeRes



    fun updateEmail(newEmail : String){
        authRepository.updateEmail(newEmail,_emailChangeRes)
    }


    fun delete(){
        authRepository.delete(_deleteRes)
    }

    fun updateUsername(newUsername: String) {
        authRepository.updateUsername(newUsername, _usernameChangeRes)
    }

    fun modifyEmail(e: String) {
        this.modifiedEmail.postValue(e)
    }


    fun changePassword(oldPass: String, newPass: String) {
        authRepository.updatePassword(oldPass, newPass, _passwordChangeRes)
    }

    fun updatePicture(uri: Uri) {
        authRepository.updatePicture(uri, _pictureChangeRes)
    }


    /**
     * Set the LiveData variable changeForm.value with right ChangePasswordForm state value for displaying the password error in the view
     * @param oldPswd Password to be changed
     * @param newPswd New password
     * @param rptPswd New password again
     */
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

    fun invalidatePasswordFormState(){
        changeForm.value = ChangePasswordFormState(isDataValid = false)
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}