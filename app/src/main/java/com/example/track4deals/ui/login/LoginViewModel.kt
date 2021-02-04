package com.example.track4deals.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.track4deals.R
import com.example.track4deals.data.models.FirebaseOperationResponse
import com.example.track4deals.data.models.LoginFormState
import com.example.track4deals.data.models.LoginResult
import com.example.track4deals.data.models.RegisterResult
import com.example.track4deals.data.repository.AuthRepository
import kotlinx.coroutines.launch


class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _changePsw = MutableLiveData<LoginFormState>()
    val changePswState: LiveData<LoginFormState> = _changePsw

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val  _changePswResponse = MutableLiveData<FirebaseOperationResponse>()
    val changePswResponse : LiveData<FirebaseOperationResponse> = _changePswResponse

    fun login(email: String, password: String) {
        authRepository.login(email, password, _loginResult)
    }

    fun loginWithGoogle(idToken: String) {
        authRepository.loginWithGoogle(idToken, _loginResult)
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            authRepository.resetPassword(email, _changePswResponse)
        }
    }


    fun loginDataChanged(email: String, password: String) {
        if (!isEmailValid(email)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_email)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    fun forgotPasswordDataChanged(email: String) {
        if (!isEmailValid(email)) {
            _changePsw.value = LoginFormState(usernameError = R.string.invalid_email)
        } else {
            _changePsw.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder email validation check
    private fun isEmailValid(email: String): Boolean {
        return if (email.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            false
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }


}
