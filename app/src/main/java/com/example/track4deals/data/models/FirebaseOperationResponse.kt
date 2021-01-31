package com.example.track4deals.data.models

data class FirebaseOperationResponse(
    val status : Boolean,
    val type: FirebaseOperation,
    val message : String,
)

enum class FirebaseOperation {
    UPDATEUSERNAME, UPDATEPIC, UPDATEEMAIL, UPDATEPASSWORD, RESETPASSWORD, DELETE
}