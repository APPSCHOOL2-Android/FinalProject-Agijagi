package likelion.project.agijagi.wishlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import likelion.project.agijagi.databinding.ItemWishListBinding

class WishListAdapter() :
    ListAdapter<WishListModel, WishListAdapter.WishListViewHolder>(diffUtil) {

    inner class WishListViewHolder(val bind: ItemWishListBinding) :
        RecyclerView.ViewHolder(bind.root) {

        fun bind(item: WishListModel) {
            with(bind) {
                textviewWishListBrand.text = item.brand
                textviewWishListName.text = item.name
                textviewWishListPrice.text = item.price
            }

            bind.buttonWishListFavorite.setOnClickListener {
                it.isSelected = it.isSelected != true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishListViewHolder {
        val itemWishListBinding =
            ItemWishListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = WishListViewHolder(itemWishListBinding)

        return viewHolder
    }

    override fun onBindViewHolder(holder: WishListViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<WishListModel>() {
            override fun areItemsTheSame(
                oldItem: WishListModel,
                newItem: WishListModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: WishListModel,
                newItem: WishListModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}