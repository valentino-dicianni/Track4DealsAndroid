package com.example.track4deals.data.models
/**
 * Data Class used for representing the result of Firebase operation
 * @param status indicate if the operation was successful or not
 * @param type indicate the type of operation performed on Firebase represented as FirebaseOperation enum
 * @param message message returned by firebase if any
 */
data class FirebaseOperationResponse(
    val status : Boolean,
    val type: FirebaseOperation,
    val message : String,
)

enum class FirebaseOperation {
    UPDATEUSERNAME, UPDATEPIC, UPDATEEMAIL, UPDATEPASSWORD, RESETPASSWORD, DELETE
}