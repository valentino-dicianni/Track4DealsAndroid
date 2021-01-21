package com.example.track4deals.ui.offers


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.track4deals.R
import com.example.track4deals.data.database.entity.ProductEntity
import com.example.track4deals.internal.ScopedFragment
import com.example.track4deals.internal.TokenProvider
import com.example.track4deals.ui.offers.recyclerView.OnProductListener
import com.example.track4deals.ui.offers.recyclerView.ProductListItem
import com.example.track4deals.ui.offers.recyclerView.TopSpacingItemDecoration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_offers.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class OffersFragment :  ScopedFragment(), KodeinAware, OnProductListener {
    override val kodein by closestKodein()
    private lateinit var offersViewModel: OffersViewModel
    private val offersViewModelFactory: OffersViewModelFactory by instance()
    private val tokenProvider : TokenProvider by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        offersViewModel = ViewModelProvider(this, offersViewModelFactory).get(OffersViewModel::class.java)
        bindUI(this)
        return inflater.inflate(R.layout.fragment_offers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeContainer.setOnRefreshListener {
            bindUI(this)
            swipeContainer.isRefreshing = false
        }

    }

    private fun bindUI(listener: OnProductListener) = launch(Dispatchers.Main) {
        val offers = offersViewModel.offers.await()
        if(tokenProvider.get() != "") {
            val tracking = offersViewModel.trackings.await()
        }
        offers.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            group_loading.visibility = View.GONE
            initRecyclerView(it.toItemsList(listener))
        })
    }

    private fun List<ProductEntity>.toItemsList(listener: OnProductListener): List<ProductListItem> {
        return this.map {
            context?.let { ctx -> ProductListItem(it, ctx, listener, tokenProvider) }!!
        }
    }

    private fun initRecyclerView(items: List<ProductListItem>) {
        val groupAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(items)
        }
        items_linear_rv.apply {
            adapter = groupAdapter
            addItemDecoration(TopSpacingItemDecoration(30))
            items_linear_rv.layoutManager = LinearLayoutManager(context)
            items_linear_rv.setHasFixedSize(true)
        }

    }

    override fun onUrlClick(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    override fun onAddTracking(product: ProductEntity) {
        launch(Dispatchers.Main) {
            offersViewModel.addTracking(product)
            Toast.makeText(context, getString(R.string.track_added), Toast.LENGTH_LONG).show()
        }
    }

    override fun onRemoveTracking(product: ProductEntity) {
        launch(Dispatchers.Main) {
            offersViewModel.removeTracking(product)
            Toast.makeText(context, getString(R.string.track_remove), Toast.LENGTH_LONG).show()
        }
    }
}