package likelion.project.agijagi.order


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import likelion.project.agijagi.databinding.ItemOrderBinding
import androidx.recyclerview.widget.DiffUtil
import likelion.project.agijagi.R

class OrderAdapter() : ListAdapter<OrderModel, OrderAdapter.OrderViewHolder>(diffUtil) {

    inner class OrderViewHolder(val orderBinding: ItemOrderBinding) :
        RecyclerView.ViewHolder(orderBinding.root) {

        fun bind(item: OrderModel) {
            with(orderBinding) {
                textViewOrderState.text = item.state
                textViewOrderBrand.text = item.brand
                textViewOrderName.text = item.name
                textViewOrderPrice.text = item.price
            }
        }

        init{
            orderBinding.root.setOnClickListener {
                if(adapterPosition != RecyclerView.NO_POSITION){

                }
                it.findNavController().navigate(R.id.action_orderFragment_to_orderDetailFragment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val itemOrderBinding =
            ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = OrderViewHolder(itemOrderBinding)

        return viewHolder
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<OrderModel>() {
            override fun areItemsTheSame(
                oldItem: OrderModel,
                newItem: OrderModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: OrderModel,
                newItem: OrderModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }


}