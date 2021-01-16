package com.example.track4deals.data.database.entity
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.sql.Date
import java.time.OffsetDateTime

@Entity(tableName = "product")
data class ProductEntity(
    @PrimaryKey(autoGenerate = false)
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
    val isDeal: Int,
    val is_tracking: Int
    /*val last_modify: String,*/
) {

}