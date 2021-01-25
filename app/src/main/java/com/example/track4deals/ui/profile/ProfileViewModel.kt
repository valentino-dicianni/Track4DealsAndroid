package com.example.track4deals.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.track4deals.R
import com.example.track4deals.data.repository.LoginRepository
import com.example.track4deals.data.models.ChangePasswordFormState
import com.example.track4deals.data.repository.UserRepository
import com.example.track4deals.internal.lazyDeferred

class ProfileViewModel(
    private  val userRepository: UserRepository
) : ViewModel() {

    private val changeForm = MutableLiveData<ChangePasswordFormState>()
    val changeFormState: LiveData<ChangePasswordFormState> = changeForm


    val user by lazyDeferred {
        userRepository.getUser()
    }


    //INPUT:    String value from change password text field
    //OUTPUT:   No returned value.
    //Set the LiveData variable changeForm.value with right ChangePasswordForm state value for displaying the right error in the view
    fun passwdDataChanged(oldPswd: String, newPswd: String, rptPswd: String) {

        if(!isPasswordValid(oldPswd)) {
            changeForm.value = ChangePasswordFormState(oldPasswordError = R.string.invalid_password)
        } else if (!isPasswordValid(newPswd)) {
            changeForm.value = ChangePasswordFormState(newPasswordError = R.string.invalid_password)
        }else if (!isPasswordValid(rptPswd)) {
            changeForm.value = ChangePasswordFormState(repeatPasswordError = R.string.invalid_password)
        }else if (newPswd != rptPswd) {
            changeForm.value = ChangePasswordFormState(repeatPasswordError = R.string.pswd_must_be_equal)
        }
        else {
            changeForm.value = ChangePasswordFormState(isDataValid = true)
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }



}