package com.example.track4deals.data.models

data class FirebaseOperationResponse(
        val status : Boolean,
        val response: FirebaseOperation,
)

enum class FirebaseOperation {
    UPDATEUSERNAME, UPDATEPIC, UPDATEEMAIL, UPDATEPASSWORD, RESETPASSWORD, DELETE
}