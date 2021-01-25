package com.example.track4deals.data.models

data class ServerResponseUser (
    val ok : String,
    val err: String,
    val response: UserInfo?
    ){}