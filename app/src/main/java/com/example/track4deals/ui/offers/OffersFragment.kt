package com.example.track4deals.ui.offers

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.track4deals.R
import com.example.track4deals.ui.offers.recyclerView.ItemsAdapter
import com.example.track4deals.ui.offers.recyclerView.OnLoadMoreListener
import com.example.track4deals.ui.offers.recyclerView.RecyclerViewLoadMoreScroll
import kotlinx.android.synthetic.main.fragment_offers.*

class OffersFragment : Fragment() {

    private lateinit var offersViewModel: OffersViewModel
    lateinit var itemsCells: ArrayList<String?>
    lateinit var loadMoreItemsCells: ArrayList<String?>
    lateinit var adapterLinear: ItemsAdapter
    lateinit var scrollListener: RecyclerViewLoadMoreScroll
    lateinit var mLayoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        offersViewModel = ViewModelProvider(this).get(OffersViewModel::class.java)
        return inflater.inflate(R.layout.fragment_offers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setItemsData()
        setAdapter()
        setRVLayoutManager()
        setRVScrollListener()
    }

    private fun setItemsData() {
        itemsCells = ArrayList()
        for (i in 0..40) {
            itemsCells.add("Product $i")
        }
    }

    private fun setAdapter() {
        adapterLinear = ItemsAdapter(itemsCells)
        adapterLinear.notifyDataSetChanged()
        items_linear_rv.adapter = adapterLinear

    }

    private fun setRVLayoutManager() {
        mLayoutManager = LinearLayoutManager(context)
        items_linear_rv.layoutManager = mLayoutManager
        items_linear_rv.setHasFixedSize(true)
    }

    private fun setRVScrollListener() {
        mLayoutManager = LinearLayoutManager(context)
        scrollListener = RecyclerViewLoadMoreScroll(mLayoutManager as LinearLayoutManager)
        scrollListener.setOnLoadMoreListener(object :
            OnLoadMoreListener {
            override fun onLoadMore() {
                LoadMoreData()
            }
        })
        items_linear_rv.addOnScrollListener(scrollListener)
    }

    private fun LoadMoreData() {
        //Add the Loading View
        adapterLinear.addLoadingView()
        //Create the loadMoreItemsCells Arraylist
        loadMoreItemsCells = ArrayList()
        //Get the number of the current Items of the main Arraylist
        val start = adapterLinear.itemCount
        //Load 16 more items
        val end = start + 16
        //Use Handler if the items are loading too fast.
        //If you remove it, the data will load so fast that you can't even see the LoadingView
        Handler(Looper.getMainLooper()).postDelayed({
            for (i in start..end) {
                //Get data and add them to loadMoreItemsCells ArrayList
                loadMoreItemsCells.add("Item $i")
            }
            //Remove the Loading View
            adapterLinear.removeLoadingView()
            //We adding the data to our main ArrayList
            adapterLinear.addData(loadMoreItemsCells)
            //Change the boolean isLoading to false
            scrollListener.setLoaded()
            //Update the recyclerView in the main thread
            items_linear_rv.post {
                adapterLinear.notifyDataSetChanged()
            }
        }, 3000)

    }


}