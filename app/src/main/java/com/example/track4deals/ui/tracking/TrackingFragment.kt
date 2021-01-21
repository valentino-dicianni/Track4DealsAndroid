package com.example.track4deals.ui.tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.track4deals.R
import com.example.track4deals.internal.ScopedFragment
import kotlinx.android.synthetic.main.fragment_offers.group_loading
import kotlinx.android.synthetic.main.fragment_tracking.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrackingFragment : ScopedFragment() {

    private lateinit var trackingViewModel: TrackingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        trackingViewModel =
            ViewModelProvider(this).get(TrackingViewModel::class.java)
        return inflater.inflate(R.layout.fragment_tracking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        group_loading.visibility = View.GONE

        track_btn.setOnClickListener {
            val link = edit_text_link.text.toString()
            if (link != "") {
                group_loading.visibility = View.VISIBLE
                launch(Dispatchers.Main) {
                    trackingViewModel.trackProduct(link)
                    group_loading.visibility = View.GONE
                    // TODO: dipende dal risultato
                    Toast.makeText(context, "Fine", Toast.LENGTH_LONG).show()
                }
            }

        }
    }
}