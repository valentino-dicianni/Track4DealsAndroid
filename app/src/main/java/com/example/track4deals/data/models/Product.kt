package com.example.track4deals.data.models

import com.example.track4deals.data.database.entity.ProductEntity

data class Product(
    val ASIN: String,
    val product_url: String,
    val title: String,
    val brand: String,
    val category: String,
    val description: String,
    val normal_price: Double,
    val offer_price: Double,
    val discount_perc: Double,
    val imageUrl_large: String,
    val imageUrl_medium: String,
    val isDeal: Boolean
){
    fun productToEntity( tracking : Int) : ProductEntity {
        val  deal = if (this.isDeal) 1
        else 0
        return ProductEntity(
            ASIN,
            product_url,
            title,
            brand,
            category,
            description,
            normal_price,
            offer_price,
            discount_perc,
            imageUrl_large,
            imageUrl_medium,
            deal,
            tracking
        )
    }
}