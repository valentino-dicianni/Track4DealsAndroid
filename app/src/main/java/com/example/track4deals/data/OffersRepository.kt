package com.example.track4deals.data


import androidx.lifecycle.LiveData
import com.example.track4deals.data.database.ProductDAO
import com.example.track4deals.data.database.entity.ProductEntity
import com.example.track4deals.data.models.Product
import com.example.track4deals.data.models.ServerResponse
import com.example.track4deals.services.OffersDataService
import com.example.track4deals.services.OffersService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OffersRepository(
    private val productDAO: ProductDAO,
    private val offersDataService : OffersDataService
) {

    init {
        offersDataService.downloadedOffers.observeForever { newOffers ->
            persistOffersData(newOffers)
        }
    }

    private fun persistOffersData(newOffers: ServerResponse?) {
        GlobalScope.launch(Dispatchers.IO) {
            val products: ArrayList<Product>? = newOffers?.response
            var deal: Int
            if (products != null) {
                for (p: Product in products) {
                    deal = if(p.isDeal) 1
                    else 0
                    val newP = ProductEntity(
                        p.ASIN,
                        p.product_url,
                        p.title,
                        p.brand,
                        p.category,
                        p.description,
                        p.normal_price,
                        p.offer_price,
                        p.discount_perc,
                        p.imageUrl_large,
                        p.imageUrl_medium,
                        deal
                    )
                    productDAO.upsert(newP)
                }
            }
        }
    }

    suspend fun getOffers(): LiveData<List<ProductEntity>> {
        return withContext(Dispatchers.IO) {
            initOffersData()
            return@withContext productDAO.getAllProduct()
        }
    }

    private suspend fun initOffersData() {
        offersDataService.getOffers()
    }
}