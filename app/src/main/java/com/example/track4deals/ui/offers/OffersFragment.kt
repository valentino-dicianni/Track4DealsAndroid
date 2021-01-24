package com.example.track4deals.ui.offers


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.track4deals.R
import com.example.track4deals.data.constants.AppConstants.Companion.SERVER_OK
import com.example.track4deals.data.database.entity.ProductEntity
import com.example.track4deals.internal.ScopedFragment
import com.example.track4deals.internal.UserProvider
import com.example.track4deals.ui.offers.recyclerView.ExpandableHeaderItem
import com.example.track4deals.ui.offers.recyclerView.OnProductListener
import com.example.track4deals.ui.offers.recyclerView.ProductListItem
import com.example.track4deals.ui.offers.recyclerView.TopSpacingItemDecoration
import com.xwray.groupie.*
import kotlinx.android.synthetic.main.fragment_offers.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class OffersFragment : ScopedFragment(), KodeinAware, OnProductListener {
    override val kodein by closestKodein()
    private lateinit var offersViewModel: OffersViewModel
    private val offersViewModelFactory: OffersViewModelFactory by instance()
    private val userProvider: UserProvider by instance()

    private lateinit var groupAdapter: GroupAdapter<ViewHolder>
    private var numTracking : Int = 0
    private var numOffers : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_offers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        offersViewModel =
            ViewModelProvider(this, offersViewModelFactory).get(OffersViewModel::class.java)

        groupAdapter = GroupAdapter<ViewHolder>()
        items_linear_rv.apply {
            addItemDecoration(TopSpacingItemDecoration(30))
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
            setHasFixedSize(true)
        }
        bindUI(this)

        swipeContainer.setOnRefreshListener {
            groupAdapter = GroupAdapter<ViewHolder>()
            items_linear_rv.adapter = groupAdapter
            numOffers = 0
            numTracking = 0
            bindUI(this)
            swipeContainer.isRefreshing = false
        }
    }

    private fun bindUI(listener: OnProductListener) = launch(Dispatchers.Main) {
        Log.d("TAG", "bindUI: CHIAMATAAA")
        val offers = offersViewModel.offers.await()
        offers.observe(viewLifecycleOwner, Observer {
            Log.d("TAG", "bindUI: offers -> ${it.size}")
            if (it == null) return@Observer // gestrire null
            if (!userProvider.isLoggedIn()) {
                group_loading.visibility = View.GONE
            }
            if(numOffers < 1)
                addOffersRecyclerView(it.toItemsList(listener))
        })
        if (userProvider.isLoggedIn()) {
            val trackings = offersViewModel.trackings.await()
            trackings.observe(viewLifecycleOwner, Observer {
                Log.d("TAG", "bindUI: tracking -> ${it.size}")
                if (it == null) return@Observer // gestrire null
                userProvider.setNumTracking(it.size)
                group_loading.visibility = View.GONE
                if(numTracking < 1)
                    addTrackingRecyclerView(it.toItemsList(listener))
            })
        }
    }

    private fun List<ProductEntity>.toItemsList(listener: OnProductListener): List<ProductListItem> {
        return this.map {
            context?.let { ctx -> ProductListItem(it, ctx, listener, userProvider) }!!
        }
    }

    private fun addOffersRecyclerView(
        itemsOffers: List<ProductListItem>,
    ) {
        ExpandableGroup(
            context?.let { ExpandableHeaderItem(it.getString(R.string.title_offers)) },
            true
        ).apply {
            add(Section(itemsOffers))
            groupAdapter.add(this)
        }
        numOffers++
    }

    private fun addTrackingRecyclerView(
        itemsTracking: List<ProductListItem>
    ) {
        ExpandableGroup(
            context?.let { ExpandableHeaderItem(it.getString(R.string.title_tracking)) },
            false
        ).apply {
            add(Section(itemsTracking))
            groupAdapter.add(this)
        }
        numTracking++
    }

    override fun onUrlClick(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    override fun onAddTracking(product: ProductEntity) {
        launch(Dispatchers.Main) {
            offersViewModel.addTrackingProduct = product
            val serverRes = offersViewModel.addTrackingRes.await()
            if (serverRes.value?.ok == SERVER_OK) {
                Toast.makeText(context, getString(R.string.track_added), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, serverRes.value?.err, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onRemoveTracking(product: ProductEntity) {
        launch(Dispatchers.Main) {
            offersViewModel.removeTrackingProduct = product
            val serverRes = offersViewModel.removeTrackingRes.await()
            if (serverRes.value?.ok == SERVER_OK) {
                Toast.makeText(context, getString(R.string.track_remove), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, serverRes.value?.err, Toast.LENGTH_LONG).show()
            }
        }
    }
}