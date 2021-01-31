package com.example.track4deals.ui.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.track4deals.R
import com.example.track4deals.data.models.LoginFormState
import com.example.track4deals.data.models.RegisterFormState
import com.example.track4deals.data.models.RegisterResult
import com.example.track4deals.data.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _registerForm = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _registerForm

    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult

    fun registerUserServer(username: String, email: String, password: String) {
        viewModelScope.launch {
            authRepository.registerUser(username, email, password, _registerResult)
        }

    }

    fun registerDataChanged(username: String, email: String, password1: String, password2: String) {
        if (!isUsernameValid(username)) {
            _registerForm.value = RegisterFormState(usernameError = R.string.invalid_username)
        } else if (!isEmailValid(email)) {
            _registerForm.value = RegisterFormState(emailError = R.string.invalid_email)
        } else if (!isPasswordValid(password1)) {
            _registerForm.value = RegisterFormState(passwordError = R.string.invalid_password)
        } else if (!isPasswordValid(password2)) {
            _registerForm.value = RegisterFormState(password2Error = R.string.invalid_password)
        } else if (!isSamePassword(password1, password2)) {
            _registerForm.value = RegisterFormState(password2Error = R.string.notSamePsw)
        } else {
            _registerForm.value = RegisterFormState(isDataValid = true)
        }
    }

    private fun isUsernameValid(username: String): Boolean {
        return username.length > 1
    }

    // A placeholder username validation check
    private fun isEmailValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            false
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun isSamePassword(password1: String, password2: String): Boolean {
        return password1 == password2
    }

}