package likelion.project.agijagi.shopping

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.ItemShoppingListBinding

class ShoppingListAdapter(val context: Context) :
    ListAdapter<ShoppingListModel, ShoppingListAdapter.ShoppingListViewHolder>(diffUtil) {

    lateinit var actionCheckBoxParentState: () -> Unit

    inner class ShoppingListViewHolder(val bind: ItemShoppingListBinding) :
        RecyclerView.ViewHolder(bind.root) {

        fun bind(item: ShoppingListModel) {
            with(bind) {
                val params = checkboxShoppingListItem.layoutParams
                checkboxShoppingListItem.layoutParams = params

                checkboxShoppingListItem.isChecked = item.isCheck
                actionCheckBoxParentState?.invoke()

                textviewShoppingListItemBrand.text = item.brand
                textviewShoppingListItemName.text = item.name
                textviewShoppingListItemPrice.text = item.price


            }

            bind.buttonChangeOption.setOnClickListener {
                it.findNavController()
                    .navigate(R.id.action_shoppingListFragment_to_customOptionFragment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val itemShoppingListBinding =
            ItemShoppingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ShoppingListViewHolder(itemShoppingListBinding)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        holder.bind(currentList[position])
        holder.bind.checkboxShoppingListItem.setOnClickListener{
            currentList[position].isCheck = !currentList[position].isCheck
            actionCheckBoxParentState()
        }
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

    fun setCheckBoxParentState(action: () -> Unit) {
        actionCheckBoxParentState = action
    }
}