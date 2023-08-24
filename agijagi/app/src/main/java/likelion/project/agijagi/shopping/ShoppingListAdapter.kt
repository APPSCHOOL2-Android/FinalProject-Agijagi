package likelion.project.agijagi.shopping

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import likelion.project.agijagi.databinding.ItemShoppingListBinding

class ShoppingListAdapter() :
    ListAdapter<ShoppingListModel, ShoppingListAdapter.ShoppingListViewHolder>(diffUtil) {

    inner class ShoppingListViewHolder(val bind: ItemShoppingListBinding) :
        RecyclerView.ViewHolder(bind.root) {

        fun bind(item: ShoppingListModel) {
            with(bind) {
                textviewShoppingListItemBrand.text = item.brand
                textviewShoppingListItemName.text = item.name
                textviewShoppingListItemPrice.text = item.price
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val itemShoppinglistBinding =
            ItemShoppingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = ShoppingListViewHolder(itemShoppinglistBinding)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ShoppingListModel>() {
            override fun areItemsTheSame(
                oldItem: ShoppingListModel,
                newItem: ShoppingListModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ShoppingListModel,
                newItem: ShoppingListModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}