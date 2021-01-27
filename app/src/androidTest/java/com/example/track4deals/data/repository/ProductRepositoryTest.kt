package com.example.track4deals.data.repository

import androidx.test.platform.app.InstrumentationRegistry
import com.example.track4deals.data.constants.AppConstants
import com.example.track4deals.data.models.Product
import com.example.track4deals.services.OffersService
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import retrofit2.HttpException

class ProductRepositoryTest : TestCase(), KodeinAware {
    override val kodein by closestKodein(InstrumentationRegistry.getInstrumentation().targetContext)
    private val offerService: OffersService by instance()

    lateinit var mockProduct: Product

    public override fun setUp() {
        super.setUp()
        mockProduct = Product(
            "B084DWG2VQ",
            "https://www.amazon.it/dp/B084DWG2VQ?tag=danieleg-21&linkCode=ogi&th=1&...",
            "Nuovo Echo Dot (4ª generazione) - Altoparlante intelligente con Alexa ...",
            "Amazon",
            "Electronics",
            "Ti presentiamo il nuovo Echo Dot Il nostro altoparlante intelligente c...",
            59.0,
            39.99,
            33.0,
            "https://m.media-amazon.com/images/I/51fsVTWWlPL.jpg",
            "https://m.media-amazon.com/images/I/51fsVTWWlPL._SL160_.jpg",
            true
        )
    }


    public override fun tearDown() {}

    fun testGetOffers() = runBlocking {
        val res = offerService.getAllOffersAsync().await()
        assertEquals(res.ok, AppConstants.SERVER_OK)
        assert(res.response?.size!! >= 0)
    }

    fun testGetTrackingProducts() = runBlocking {
        try {
            val res = offerService.getAllTrackingAsync().await()
        } catch (e: HttpException) {
            assert(e.code() == 401)
        }
    }

    fun testAddTrackingProduct() = runBlocking {
        try {
            val res = offerService.addTrackingProductAsync(
                "B084DWG2VQ",
                "https://www.amazon.it/dp/B084DWG2VQ?tag=danieleg-21&linkCode=ogi&th=1&...",
                "Nuovo Echo Dot (4ª generazione) - Altoparlante intelligente con Alexa ...",
                "Amazon",
                "Electronics",
                "Ti presentiamo il nuovo Echo Dot Il nostro altoparlante intelligente c...",
                59.0,
                39.99,
                33.0,
                "https://m.media-amazon.com/images/I/51fsVTWWlPL.jpg",
                "https://m.media-amazon.com/images/I/51fsVTWWlPL._SL160_.jpg",
                true
            ).await()
        } catch (e: HttpException) {
            assert(e.code() == 401)
        }
    }

    fun testRemoveTrackingProduct() = runBlocking {
        try {
            val res = offerService.removeTrackingProductAsync(
                "B084DWG2VQ",
                "https://www.amazon.it/dp/B084DWG2VQ?tag=danieleg-21&linkCode=ogi&th=1&...",
                "Nuovo Echo Dot (4ª generazione) - Altoparlante intelligente con Alexa ...",
                "Amazon",
                "Electronics",
                "Ti presentiamo il nuovo Echo Dot Il nostro altoparlante intelligente c...",
                59.0,
                39.99,
                33.0,
                "https://m.media-amazon.com/images/I/51fsVTWWlPL.jpg",
                "https://m.media-amazon.com/images/I/51fsVTWWlPL._SL160_.jpg",
                true
            ).await()
        } catch (e: HttpException) {
            assert(e.code() == 401)
        }
    }

    fun testFindProductByAsin() = runBlocking {
        try {
            val res = offerService.verifyProductAsync(
                "B084DWG2VQ"
            ).await()
        } catch (e: HttpException) {
            assert(e.code() == 401)
        }
    }
}