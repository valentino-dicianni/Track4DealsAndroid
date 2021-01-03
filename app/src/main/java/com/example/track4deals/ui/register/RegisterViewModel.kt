package com.example.track4deals.ui.register

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.track4deals.R
import com.example.track4deals.data.models.LoginFormState
import com.example.track4deals.data.models.LoginResult
import com.example.track4deals.services.AuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val authService = AuthService.AuthServiceCreator.newService()

    fun addUser(displayName: String, email: String, password: String){
        this.viewModelScope.launch(Dispatchers.IO) {
            val response = authService.registerNewUser(displayName, password, email)
            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d("USER", "user id = ${it.user_id} profilePhoto = ${it.profilePhoto} cat_list = ${it.category_list}")
                }
            } else {
                Log.d("USER", "Error msg = ${response.message()}")
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

}