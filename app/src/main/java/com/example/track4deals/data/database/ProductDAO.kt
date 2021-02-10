package com.example.track4deals.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.track4deals.data.database.entity.ProductEntity
import com.example.track4deals.data.models.Product
import kotlinx.coroutines.flow.Flow


@Dao
interface ProductDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(productEntity: ProductEntity)


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(obj: ProductEntity?): Long


    @Query("UPDATE product SET  normal_price = :normal_price, offer_price = :offer_price, discount_perc = :discount_perc, isDeal = :isDeal WHERE ASIN = :ASIN")
    fun update(
        ASIN: String,
        normal_price: Double,
        offer_price: Double,
        discount_perc: Double,
        isDeal: Int
    )

    @Query("update product set is_tracking = 0")
    fun resetTracking();

    @Query("select * from product where isDeal = 1")
    fun getAllProduct(): Flow<List<ProductEntity>>

    @Query("select * from product where is_tracking = 1")
    fun getAllTracking(): Flow<List<ProductEntity>>


    @Transaction
    fun upsertAll(products : List<Product>, isTracking: Int) {
        for (p : Product in products){
            upsert(p.productToEntity(isTracking))
        }
    }

    @Transaction
    fun upsertAllCustom(products : List<Product>, isTracking: Int) {
        for (p : Product in products){
            val obj :ProductEntity = p.productToEntity(isTracking)
            val id = insert(obj)
            if (id == -1L) {
                update(obj.ASIN, obj.normal_price, obj.offer_price, obj.discount_perc, obj.isDeal)
            }
        }
    }

}