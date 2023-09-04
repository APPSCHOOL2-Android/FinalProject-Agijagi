package likelion.project.agijagi.sellermypage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import likelion.project.agijagi.databinding.ItemOrderManagementBinding
import likelion.project.agijagi.databinding.ItemShoppingListBinding
import likelion.project.agijagi.sellermypage.model.OrderManagementModel
import likelion.project.agijagi.shopping.ShoppingListModel


class OrderManagementAdapter(val itemClick: (OrderManagementModel) -> Unit) :
    ListAdapter<OrderManagementModel, OrderManagementAdapter.OrderManagementHolder>(diffUtil) {

    inner class OrderManagementHolder(val bind: ItemOrderManagementBinding) :
        RecyclerView.ViewHolder(bind.root) {
        fun bind(item: OrderManagementModel) {
            with(bind) {
                textviewOrderManagementItemBrand.text = item.brand
                textviewOrderManagementItemName.text = item.name
            }

            bind.root.setOnClickListener {
                itemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderManagementAdapter.OrderManagementHolder {
        val itemOrderManagementBinding =
            ItemOrderManagementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderManagementHolder(itemOrderManagementBinding)
    }

    override fun onBindViewHolder(
        holder: OrderManagementAdapter.OrderManagementHolder,
        position: Int
    ) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<OrderManagementModel>() {
            override fun areItemsTheSame(
                oldItem: OrderManagementModel,
                newItem: OrderManagementModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: OrderManagementModel,
                newItem: OrderManagementModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}