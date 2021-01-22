package com.example.track4deals.data.models

/**
 * Data validation state of the change password form.
 */
data class ChangePasswordFormState (
        val oldPasswordError: Int? = null,
        val newPasswordError: Int? = null,
        val repeatPasswordError: Int? = null,
        val isDataValid: Boolean = false
)
