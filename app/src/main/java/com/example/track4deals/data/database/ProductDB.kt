package com.example.track4deals.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.track4deals.data.database.entity.ProductEntity

@Database(
    entities = [ProductEntity::class],
    version = 1
)
@TypeConverters(DateConverter::class)

abstract class ProductDB : RoomDatabase() {
    abstract fun productDAO(): ProductDAO

    companion object {
        @Volatile
        private var instance: ProductDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ProductDB::class.java, "productDatabase2.db"
            ).build()
    }
}