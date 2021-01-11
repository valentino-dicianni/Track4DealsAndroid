package com.example.track4deals.data.models

data class ServerResponse(
    val ok : String,
    val err: String,
    val response: ArrayList<Product>?
) {}
