package likelion.project.agijagi.wishlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.ItemWishListBinding

class WishListAdapter() :
    ListAdapter<WishListModel, WishListAdapter.ShoppingListViewHolder>(diffUtil) {

    inner class ShoppingListViewHolder(val bind: ItemWishListBinding) :
        RecyclerView.ViewHolder(bind.root) {

        fun bind(item: WishListModel) {
            with(bind) {
                textviewWishListBrand.text = item.brand
                textviewWishListName.text = item.name
                textviewWishListPrice.text = item.price
            }

            bind.run {
                buttonWishListFavorite.setOnClickListener {
                    it.isSelected = it.isSelected != true
                }

                // 추후 기성품, 주문 제작 상품 구분 필요
                root.setOnClickListener {
                    // 기성품일 떄
                    it.findNavController().navigate(R.id.action_wishListFragment_to_productDetailFragment)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val itemWishListBinding =
            ItemWishListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = ShoppingListViewHolder(itemWishListBinding)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
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