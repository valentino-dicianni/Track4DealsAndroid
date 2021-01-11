package com.example.track4deals.ui.offers.recyclerView

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.track4deals.R
import com.example.track4deals.data.database.entity.ProductEntity
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.element_row_rv.view.*


class ProductListItem(
    var productEntity: ProductEntity
) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            viewHolder.itemView.productName.text = productEntity.title
            viewHolder.itemView.productDescription.text = productEntity.description
            updateImage()
        }
    }

    override fun getLayout() = R.layout.element_row_rv

    private fun ViewHolder.updateImage() {
        // per le immagini usiamo Glide -> servono opzioni in caso non sia diponibile l'immagine
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)

        Glide.with(this.containerView)
            .applyDefaultRequestOptions(requestOptions)
            .load(productEntity.imageUrl_large)
            .into(itemView.productImage)
    }
}