package com.example.track4deals.data.database
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.track4deals.data.database.entity.ProductEntity

@Dao
interface ProductDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(productEntity: ProductEntity)

    @Query("select * from product where isDeal = 1")
    fun getAllProduct(): LiveData<List<ProductEntity>>

    @Query("select * from product where is_tracking = 1")
    fun getAllTracking(): LiveData<List<ProductEntity>>
}