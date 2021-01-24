package com.example.track4deals.ui.tracking

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.track4deals.R
import com.example.track4deals.data.constants.AppConstants.Companion.SERVER_OK
import com.example.track4deals.data.database.entity.ProductEntity
import com.example.track4deals.data.models.Product
import com.example.track4deals.data.models.ServerResponse
import com.example.track4deals.internal.ScopedFragment
import com.example.track4deals.internal.UserProvider
import com.example.track4deals.ui.login.LoginFragment.Companion.TAG
import kotlinx.android.synthetic.main.fragment_offers.group_loading
import kotlinx.android.synthetic.main.fragment_tracking.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class TrackingFragment : ScopedFragment(), KodeinAware {
    private lateinit var trackingViewModel: TrackingViewModel
    override val kodein by closestKodein()
    private val trackingViewModelFactory: TrackingViewModelFactory by instance()
    private val userProvider: UserProvider by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        trackingViewModel =
            ViewModelProvider(this, trackingViewModelFactory).get(TrackingViewModel::class.java)
        return inflater.inflate(R.layout.fragment_tracking, container, false)
    }

    @SuppressLint("ShowToast")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        group_loading.visibility = View.GONE

        trackingViewModel.verifyProdResult.observe(viewLifecycleOwner, Observer { response ->
            group_loading.visibility = View.GONE
            Log.d(TAG, "findProductDetails: ${response.toString()}")
            if (response.ok == SERVER_OK) {
                context?.let { response.response?.let { it1 -> showDialog(it1.get(0), it) } }
            } else {
                context?.let { showDialogError(it) }
            }

        })

        trackingViewModel.addTrackingRes.observe(viewLifecycleOwner, Observer { response ->
            group_loading.visibility = View.GONE

            if (response.ok == SERVER_OK) {
                Toast.makeText(context, getString(R.string.track_added), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, response.err, Toast.LENGTH_LONG).show()
            }
        })

        track_btn.setOnClickListener {
            if (userProvider.isLoggedIn()) {
                val link = edit_text_link.text.toString()
                if (link != "") {
                    val pattern = "(\\/[a-zA-Z0-9]{10})(?:[/?]|)".toRegex()
                    val ASIN = pattern.find(link)?.value?.replace("/", "")?.replace("?", "")
                    if (ASIN != null) {
                        findProductDetails(ASIN)
                        edit_text_link.setText("")
                    } else { // no ASIN in text
                        edit_text_link.setText("")
                        Toast.makeText(context, getString(R.string.errorText), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                edit_text_link.setText("")
                Toast.makeText(context, context?.getString(R.string.errorToast), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun findProductDetails(ASIN: String) {
        group_loading.visibility = View.VISIBLE
        trackingViewModel.setProduct(ASIN)
    }

    private fun onAddTracking(product: ProductEntity) {
        trackingViewModel.setTrackProduct(product)
        group_loading.visibility = View.VISIBLE
    }

    private fun showDialog(product: Product, context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.trackingDialogTitle)
        builder.setMessage(Html.fromHtml("<b>PRODOTTO: </b>" + product.title + "<br><br><b>BRAND: </b> " + product.brand))

        builder.setPositiveButton(R.string.yes) { _, _ ->
            var isDeal = 0
            if (product.isDeal) isDeal = 1
            onAddTracking(product.productToEntity(isDeal))
            edit_text_link.setText("")
        }

        builder.setNegativeButton(R.string.no) { _, _ ->
            edit_text_link.setText("")
        }

        builder.show()
    }

    private fun showDialogError(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.warning)
        builder.setMessage("Non è possibile tracciare questo prodotto!")

        builder.setNeutralButton(R.string.closeBtn) { _, _ ->
            edit_text_link.setText("")
        }
        builder.show()
    }


}