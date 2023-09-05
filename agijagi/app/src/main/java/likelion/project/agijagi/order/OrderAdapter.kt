package likelion.project.agijagi.order


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.ItemOrderBinding

class OrderAdapter : ListAdapter<OrderModel, OrderAdapter.OrderViewHolder>(diffUtil) {

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
                    // 추후 교환/반품 신청 사유 입력(확인, 취소 버튼) 다이얼로그로 수정
                    Snackbar.make(it, "교환/반품 신청이 완료되었습니다.", Snackbar.LENGTH_SHORT).show()
                }

                buttonOrderShippingTracking.setOnClickListener {
                    it.findNavController()
                        .navigate(R.id.action_orderFragment_to_deliveryTrackingFragment)
                }

                buttonOrderChat.setOnClickListener {
                    it.findNavController()
                        .navigate(R.id.action_orderFragment_to_userChatListFragment)
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