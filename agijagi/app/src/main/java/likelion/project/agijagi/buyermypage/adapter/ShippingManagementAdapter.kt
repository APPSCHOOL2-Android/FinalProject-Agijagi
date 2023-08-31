package likelion.project.agijagi.buyermypage.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import likelion.project.agijagi.R
import likelion.project.agijagi.buyermypage.model.ShippingManagementModel
import likelion.project.agijagi.databinding.ItemShippingManagementBinding

class ShippingManagementAdapter : ListAdapter<ShippingManagementModel, ShippingManagementAdapter.ShippingManagementViewHolder>(diffUtil) {

    inner class ShippingManagementViewHolder(val shippingManagementBinding: ItemShippingManagementBinding) :
        RecyclerView.ViewHolder(shippingManagementBinding.root) {

        fun bind(item: ShippingManagementModel) {
            with(shippingManagementBinding) {
                textViewShippingManagementTitle.text = item.title
                textViewShippingManagementPhone.text = item.phone
                textViewShippingManagementAddress.text = item.address
            }
        }

        init{
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShippingManagementViewHolder {
        val itemShippingManagementBinding =
            ItemShippingManagementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = ShippingManagementViewHolder(itemShippingManagementBinding)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ShippingManagementViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ShippingManagementModel>() {
            override fun areItemsTheSame(
                oldItem: ShippingManagementModel,
                newItem: ShippingManagementModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ShippingManagementModel,
                newItem: ShippingManagementModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}