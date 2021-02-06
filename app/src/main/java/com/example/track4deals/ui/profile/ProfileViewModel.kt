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
    private val passwordLive = MutableLiveData<String>()
    private val deleteLive = MutableLiveData<Boolean>()
    private var _isDeleteDialogFragment: Boolean = false
    val isDeleteDialogFragment: Boolean
        get() = _isDeleteDialogFragment


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

    private val _pictureChangeRes = MutableLiveData<FirebaseOperationResponse>()
    val pictureChangeRes: LiveData<FirebaseOperationResponse> = _pictureChangeRes


    private val changeEmailPairLiveData: LiveData<Pair<String, String>> =
        object : MediatorLiveData<Pair<String, String>>() {
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

    private val deletePairLiveData: LiveData<Pair<String, Boolean>> =
        object : MediatorLiveData<Pair<String, Boolean>>() {
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



    val updateEmailResult = changeEmailPairLiveData.switchMap {
        liveData {
            authRepository.updateEmail(it.second, it.first, _emailChangeRes)
            emit(emailChangeRes)
        }
    }

    val deleteResult = deletePairLiveData.switchMap {
        liveData {
            authRepository.delete(it.first, _deleteRes)
            emit(deleteRes)
        }
    }

    fun updateUsername(newUsername: String) {
        authRepository.updateUsername(newUsername, _usernameChangeRes)
    }

    fun modifyEmail(e: String) {
        this.modifiedEmail.postValue(e)
    }

    fun delete(d: Boolean) {
        this.deleteLive.postValue(d)
    }

    fun sendPassword(p: String) {
        this.passwordLive.postValue(p)
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

    fun deleteDialogNeeded() {
        _isDeleteDialogFragment = true
    }

    fun emailDialogNeeded() {
        _isDeleteDialogFragment = false
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}