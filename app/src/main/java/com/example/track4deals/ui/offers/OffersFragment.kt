package com.example.track4deals.ui.offers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.track4deals.R
import com.example.track4deals.data.database.entity.ProductEntity
import com.example.track4deals.internal.ScopedFragment
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

class OffersFragment :  ScopedFragment(), KodeinAware {
    override val kodein by closestKodein()
    private lateinit var offersViewModel: OffersViewModel
    private val offersViewModelFactory: OffersViewModelFactory by instance()

    lateinit var itemsCells: ArrayList<ProductEntity?>
    private lateinit var productsData: ArrayList<ProductEntity>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        offersViewModel = ViewModelProvider(this, offersViewModelFactory).get(OffersViewModel::class.java)
        return inflater.inflate(R.layout.fragment_offers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() = launch(Dispatchers.Main) {
        val offers = offersViewModel.offers.await()
        offers.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            group_loading.visibility = View.GONE
            initRecyclerView(it.toItemsList())
        })
    }

    private fun List<ProductEntity>.toItemsList(): List<ProductListItem> {
        return this.map {
            context?.let { ctx -> ProductListItem(it, offersViewModel, ctx) }!!
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
}