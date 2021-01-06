package com.example.track4deals.ui.offers.recyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.track4deals.R
import android.os.Handler
import com.example.track4deals.data.constants.AppConstants
import kotlinx.android.synthetic.main.linear_item_row.view.*

class ItemsAdapter(private var itemsCells: ArrayList<String?>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private lateinit var mcontext: Context
        class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
        class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        fun addData(dataViews: ArrayList<String?>) {
            this.itemsCells.addAll(dataViews)
            notifyDataSetChanged()
        }

        fun getItemAtPosition(position: Int): String? {
            return itemsCells[position]
        }

        fun addLoadingView() {
            //Add loading item
            Handler().post {
                itemsCells.add(null)
                notifyItemInserted(itemsCells.size - 1)
            }
        }

        fun removeLoadingView() {
            //Remove loading item
            if (itemsCells.size != 0) {
                itemsCells.removeAt(itemsCells.size - 1)
                notifyItemRemoved(itemsCells.size)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            mcontext = parent.context
            return if (viewType == AppConstants.VIEW_TYPE_ITEM) {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.linear_item_row, parent, false)
                ItemViewHolder(view)
            } else {
                val view = LayoutInflater.from(mcontext).inflate(R.layout.progress_loading, parent, false)
                LoadingViewHolder(view)
            }
        }

        override fun getItemCount(): Int {
            return itemsCells.size
        }

        override fun getItemViewType(position: Int): Int {
            return if (itemsCells[position] == null) {
                AppConstants.VIEW_TYPE_LOADING
            } else {
                AppConstants.VIEW_TYPE_ITEM
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder.itemViewType == AppConstants.VIEW_TYPE_ITEM) {
                holder.itemView.productName.text = itemsCells[position]
            }
        }
}