package com.example.track4deals.ui.tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.track4deals.R
import kotlinx.android.synthetic.main.fragment_offers.*

class TrackingFragment : Fragment() {

    private lateinit var trackingViewModel: TrackingViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        trackingViewModel =
                ViewModelProvider(this).get(TrackingViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_tracking, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        group_loading.visibility = View.GONE

    }
}