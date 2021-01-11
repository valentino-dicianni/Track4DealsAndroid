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

    //TODO: sarebbe bello in ordine di modifica
    @Query("select * from product where isDeal = 1")
    fun getAllProduct(): LiveData<List<ProductEntity>>


}