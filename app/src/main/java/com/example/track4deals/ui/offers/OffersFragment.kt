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
import com.example.track4deals.data.constants.AppConstants.Companion.SERVER_OK
import com.example.track4deals.data.database.entity.ProductEntity
import com.example.track4deals.internal.ScopedFragment
import com.example.track4deals.internal.UserProvider
import com.example.track4deals.ui.offers.recyclerView.*
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
    private var numTracking: Int = 0
    private var numOffers: Int = 0

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



        // On refresh UI
        swipeContainer.setOnRefreshListener {
            groupAdapter = GroupAdapter<ViewHolder>()
            items_linear_rv.adapter = groupAdapter
            numOffers = 0
            numTracking = 0
            bindUI(this)
            swipeContainer.isRefreshing = false
        }

        offersViewModel.addTrackingRes.observe(viewLifecycleOwner, Observer {
            if (it.ok == SERVER_OK) {
                Toast.makeText(context, getString(R.string.track_added), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, it.err, Toast.LENGTH_LONG).show()
            }
        })

        offersViewModel.removeTrackingRes.observe(viewLifecycleOwner, Observer {
            if (it.ok == SERVER_OK) {
                Toast.makeText(context, getString(R.string.track_remove), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, it.err, Toast.LENGTH_LONG).show()
            }
        })

        userProvider.loadingComplete.observe(viewLifecycleOwner, Observer {
            // Bind recycler view
            bindUI(this)
        })
    }

    /**
     * Fetch for offers and tracking products and populate the
     * recycler view with 2 sections (tracking only if user is
     * loggedIn)
     * @param listener listener interface for buttons
     * in the items of the recycler view
     */
    private fun bindUI(listener: OnProductListener) = launch(Dispatchers.Main) {
        val offers = offersViewModel.offers.await()
        offers.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer // gestrire null
            if (numOffers < 1)
                addOffersRecyclerView(it.toItemsList(listener))

        })
        if (userProvider.isLoggedIn()) {
            val trackings = offersViewModel.trackings.await()
            trackings.observe(viewLifecycleOwner, Observer {
                if (it == null) return@Observer // gestrire null
                userProvider.setNumTracking(it.size)
                if (numTracking < 1)
                    addTrackingRecyclerView(it.toItemsList(listener))
            })
        }
    }

    /**
     * Mapping function from list of ProductEntity to list of ProductListItem
     */
    private fun List<ProductEntity>.toItemsList(listener: OnProductListener): List<ProductListItem> {
        return this.map {
            context?.let { ctx -> ProductListItem(it, ctx, listener, userProvider) }!!
        }
    }

    /**
     * Add a new group called "Offerte" to the recycler view
     * @param itemsOffers item list to add in the group
     */
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
        if (!userProvider.isLoggedIn()) {
            group_loading.visibility = View.GONE
        }
    }

    /**
     * Add a new group called "Tracking" to the recycler view
     * @param itemsOffers item list to add in the group
     */
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
        group_loading.visibility = View.GONE
    }


    /**
     *
     * OnProductListener interface implementation
     * - onUrlClick: launch an intent to the url passed as parameter
     * - onAddTracking: add tracking product
     * - onRemoveTracking: remove tracking product
     *
     */

    override fun onUrlClick(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    override fun onAddTracking(product: ProductEntity) {
        offersViewModel.setAddT(product)
    }

    override fun onRemoveTracking(product: ProductEntity) {
        offersViewModel.setRemT(product)
    }

    override fun onClickImage(url: String) {
        val fullImageIntent = Intent(context, FullScreenImageViewActivity::class.java)
        fullImageIntent.putExtra("url", url)
        startActivity(fullImageIntent)
    }
}