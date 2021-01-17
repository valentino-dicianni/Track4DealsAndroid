package com.example.track4deals.ui.offers.recyclerView

import com.example.track4deals.data.database.entity.ProductEntity

interface OnProductListener {
    fun onUrlClick(url : String)
    fun onAddTracking(product : ProductEntity)
    fun onRemoveTracking(product : ProductEntity)
}