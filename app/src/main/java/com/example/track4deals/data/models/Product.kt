package com.example.track4deals.data.models

data class Product(
    val ASIN: String,
    val product_url: String,
    val title: String,
    val brand: String,
    val category: String,
    val description: String,
    val normal_price: Number,
    val offer_price: Number,
    val discount_perc: Number,
    val imageUrl_large: String,
    val imageUrl_medium: String,
    val isDeal: Boolean
)
