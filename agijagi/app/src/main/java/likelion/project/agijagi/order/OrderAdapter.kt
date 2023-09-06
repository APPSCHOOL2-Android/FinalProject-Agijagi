package likelion.project.agijagi.order


import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.ItemOrderBinding

class OrderAdapter(var context: Context) : ListAdapter<OrderModel, OrderAdapter.OrderViewHolder>(diffUtil) {

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

        init {
            orderBinding.run {
                buttonOrderExchange.setOnClickListener {
                    // 다이얼로그 커스텀 필요
                    MaterialAlertDialogBuilder(context)
                        .setTitle("교환/환불 신청")
                        .setMessage("교환/환불 신청 하시겠습니까?")
                        .setPositiveButton("확인") { _: DialogInterface, _: Int ->

                        }
                        .setNegativeButton("취소", null)
                        .show()
                }

                buttonOrderShippingTracking.setOnClickListener {
                    it.findNavController()
                        .navigate(R.id.action_orderFragment_to_deliveryTrackingFragment)
                }

                buttonOrderChat.setOnClickListener {
                    it.findNavController()
                        .navigate(R.id.action_orderFragment_to_chattingListFragment)
                }
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