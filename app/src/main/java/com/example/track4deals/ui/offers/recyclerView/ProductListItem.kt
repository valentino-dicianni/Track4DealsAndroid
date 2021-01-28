package com.example.track4deals.ui.offers.recyclerView

import android.annotation.SuppressLint
import android.content.Context
import android.text.Spannable
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.track4deals.R
import com.example.track4deals.data.database.entity.ProductEntity
import com.example.track4deals.internal.UserProvider

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.element_row_rv.view.*

class ProductListItem(
    var productEntity: ProductEntity,
    var context: Context,
    var productListener: OnProductListener,
    var userProvider: UserProvider
) : Item() {
    private val STRIKE_THROUGH_SPAN = StrikethroughSpan()

    @SuppressLint("SetTextI18n")
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            viewHolder.itemView.productName.text = productEntity.title
            viewHolder.itemView.productDescription.text = productEntity.description
            viewHolder.itemView.brand.text = "${productEntity.brand} - ${productEntity.category}"
            viewHolder.itemView.percDiscount.text = "Risparmi ${productEntity.discount_perc}%"

            if (productEntity.normal_price != productEntity.offer_price) { // Se il prodotto è in offerta
                viewHolder.itemView.oldPrice.setText(
                    "${productEntity.normal_price} €",
                    TextView.BufferType.SPANNABLE
                )
                val text = viewHolder.itemView.oldPrice.text as Spannable
                text.setSpan(STRIKE_THROUGH_SPAN, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                viewHolder.itemView.newPrice.text = "${productEntity.offer_price} €"
            } else { // Non in offerta
                viewHolder.itemView.oldPrice.text = "${productEntity.normal_price} €"
                viewHolder.itemView.newPrice.text = context.getString(R.string.notOnOffer)
                viewHolder.itemView.goToOfferBtn.text = context.getString(R.string.goToProduct)
            }
            if (userProvider.isLoggedIn()) { // Se l'utente è loggato
                if (productEntity.is_tracking == 1)
                    viewHolder.itemView.addTrackingBtn.text =
                        context.getString(R.string.remove_tracking)
                if (productEntity.is_tracking == 0)
                    viewHolder.itemView.addTrackingBtn.text =
                        context.getString(R.string.add_tracking)
            } else { // Utente non loggato
                viewHolder.itemView.addTrackingBtn.text =
                    context.getString(R.string.add_tracking)
            }

            updateImage()
        }

        viewHolder.itemView.goToOfferBtn.setOnClickListener {
            productListener.onUrlClick(productEntity.product_url)
        }

        viewHolder.itemView.addTrackingBtn.setOnClickListener {
            if (userProvider.isLoggedIn()) {
                if (viewHolder.itemView.addTrackingBtn.text == context.getString(R.string.add_tracking)) {
                    viewHolder.itemView.addTrackingBtn.text =
                        context.getString(R.string.remove_tracking)
                    productListener.onAddTracking(productEntity)
                } else {
                    viewHolder.itemView.addTrackingBtn.text =
                        context.getString(R.string.add_tracking)
                    productListener.onRemoveTracking(productEntity)
                }
            } else {
                Toast.makeText(context, context.getString(R.string.errorToast), Toast.LENGTH_LONG)
                    .show()
            }
        }
       viewHolder.itemView.productImage.setOnClickListener{
           productListener.onClickImage(productEntity.imageUrl_large)
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