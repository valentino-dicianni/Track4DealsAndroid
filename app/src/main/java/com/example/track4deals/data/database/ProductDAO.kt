package com.example.track4deals.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.track4deals.data.database.entity.ProductEntity


@Dao
interface ProductDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(productEntity: ProductEntity)


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(obj: ProductEntity?): Long


    @Query("UPDATE product SET  normal_price = :normal_price, offer_price = :offer_price, discount_perc = :discount_perc, isDeal = :isDeal WHERE ASIN = :ASIN AND offer_price <> :offer_price")
    fun update(
        ASIN: String,
        normal_price: Double,
        offer_price: Double,
        discount_perc: Double,
        isDeal: Int
    )


    @Query("select * from product where isDeal = 1")
    fun getAllProduct(): LiveData<List<ProductEntity>>

    @Query("select * from product where is_tracking = 1")
    fun getAllTracking(): LiveData<List<ProductEntity>>

    @Transaction
    fun customUpsert(obj: ProductEntity?) {
        val id = insert(obj)
        if (id == -1L) {
            if (obj != null) {
                update(obj.ASIN, obj.normal_price, obj.offer_price, obj.discount_perc, obj.isDeal)
            }
        }
    }


}