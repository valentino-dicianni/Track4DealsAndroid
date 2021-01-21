package com.example.track4deals.ui.tracking

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.track4deals.R
import com.example.track4deals.internal.ScopedFragment
import com.example.track4deals.internal.UserProvider
import com.example.track4deals.ui.login.LoginFragment.Companion.TAG
import com.example.track4deals.ui.offers.OffersViewModel
import com.example.track4deals.ui.offers.OffersViewModelFactory
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

        track_btn.setOnClickListener {
            if (userProvider.isLoggedIn()) {
                val link = edit_text_link.text.toString()
                if (link != "") {
                    val pattern = "/([a-zA-Z0-9]{10})(?:[/?]|$)".toRegex()
                    val ASIN = pattern.find(link)?.value?.replace("/", "");
                    if (ASIN != null) {
                        findProductDetails(ASIN)
                    }
                }
            } else {
                Toast.makeText(context, context?.getString(R.string.errorToast), Toast.LENGTH_LONG)
            }
        }
    }

    private fun findProductDetails(ASIN: String) = launch(Dispatchers.Main) {
        group_loading.visibility = View.VISIBLE
        trackingViewModel.setProduct(ASIN)
        val response = trackingViewModel.trackingProduct.await()
        group_loading.visibility = View.GONE

        Log.d(TAG, "findProductDetails: ${response.value.toString()}")

    }

}