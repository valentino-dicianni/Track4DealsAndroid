package com.example.track4deals.data.models

data class RegisterFormState(
    val usernameError: Int? = null,
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val password2Error: Int? = null,
    val isDataValid: Boolean = false
)
