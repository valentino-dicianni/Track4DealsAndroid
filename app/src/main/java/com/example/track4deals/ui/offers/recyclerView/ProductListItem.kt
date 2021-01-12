package com.example.track4deals.ui.offers.recyclerView

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Spannable
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.track4deals.R
import com.example.track4deals.data.database.entity.ProductEntity
import com.example.track4deals.ui.offers.OffersViewModel
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.element_row_rv.view.*
import kotlinx.android.synthetic.main.fragment_offers.*
import kotlinx.android.synthetic.main.fragment_offers.view.*
import kotlinx.coroutines.*


class ProductListItem(
    var productEntity: ProductEntity,
    var offersViewModel: OffersViewModel,
    var context: Context
) : Item() {
    private val STRIKE_THROUGH_SPAN = StrikethroughSpan()

    @SuppressLint("SetTextI18n")
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            viewHolder.itemView.productName.text = productEntity.title
            viewHolder.itemView.productDescription.text = productEntity.description
            viewHolder.itemView.brand.text = productEntity.brand
            viewHolder.itemView.oldPrice.setText(
                "${productEntity.normal_price} €",
                TextView.BufferType.SPANNABLE
            )
            val text = viewHolder.itemView.oldPrice.text as Spannable
            text.setSpan(STRIKE_THROUGH_SPAN, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            viewHolder.itemView.newPrice.text = "${productEntity.offer_price} €"
            updateImage()
        }

        viewHolder.itemView.goToOfferBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(productEntity.product_url)
            context.startActivity(intent)
        }

        viewHolder.itemView.addTrackingBtn.setOnClickListener{
            val job = GlobalScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.Default){ offersViewModel.addTracking(productEntity) }
            }

            Toast.makeText(context, "Prodotto aggiunto!", Toast.LENGTH_LONG).show()
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