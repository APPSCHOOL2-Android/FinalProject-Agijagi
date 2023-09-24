package likelion.project.agijagi.order

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.ItemOrderBinding
import likelion.project.agijagi.databinding.ItemOrderDateBinding
import java.text.SimpleDateFormat
import java.util.Locale

class OrderAdapter() :
    ListAdapter<ParentOrderItem, OrderAdapter.OrderViewHolder>(OrderViewHolder.diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val viewHolder = when (viewType) {
            0 -> {
                OrderDataViewHolder(ItemOrderBinding.inflate(layoutInflater, parent, false))
            }

            1 -> {
                OrderDateViewHolder(ItemOrderDateBinding.inflate(layoutInflater, parent, false))
            }

            else -> throw Exception()
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    override fun getItemViewType(position: Int): Int {
        return currentList[position].viewType
    }


    abstract class OrderViewHolder(private val itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        abstract fun onBind(item: ParentOrderItem)

        companion object {
            val diffUtil = object : DiffUtil.ItemCallback<ParentOrderItem>() {
                override fun areItemsTheSame(
                    oldItem: ParentOrderItem,
                    newItem: ParentOrderItem
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(
                    oldItem: ParentOrderItem,
                    newItem: ParentOrderItem
                ): Boolean {
                    return oldItem.equals(newItem)
                }
            }
        }
    }

    class OrderDataViewHolder(val orderBinding: ItemOrderBinding) :
        OrderViewHolder(orderBinding.root) {
        override fun onBind(item: ParentOrderItem) {
            val orderItem = item as OrderDataClass
            orderBinding.run {
                Glide.with(orderBinding.root)
                    .load(orderItem.thumbnailImage)
                    .into(orderBinding.imageViewOrderProduct)
                textViewOrderState.text = orderItem.state
                textViewOrderBrand.text = orderItem.brand
                textViewOrderName.text = orderItem.name
                textViewOrderPrice.text = orderItem.price
                val count = orderItem.count
                textViewOrderOption.text = "$count 개"

                buttonOrderChat.setOnClickListener {
                    // buyerId : String , sellerId  : String
                    val bundle = Bundle().apply {
                        putString("buyerId", orderItem.buyerId)
                        putString("sellerId", orderItem.sellerId)
                        putString("chatRoomTitle", orderItem.brand)
                    }
                    it.findNavController()
                        .navigate(R.id.action_orderFragment_to_chattingRoomFragment, bundle)
                }

                buttonOrderShippingTracking.setOnClickListener {
                    // state, thumbnail_image, name, date
                    val bundle = Bundle().apply {
                        putString("state", orderItem.state)
                        putString("thumbnail_image", orderItem.thumbnailImage)
                        putString("name", orderItem.name)
                    }
                    it.findNavController()
                        .navigate(R.id.action_orderFragment_to_deliveryTrackingFragment, bundle)
                }
            }
        }

        init {
            orderBinding.run {
                buttonOrderExchange.setOnClickListener {
                    // 다이얼로그 커스텀 필요
                    MaterialAlertDialogBuilder(orderBinding.root.context)
                        .setTitle("교환/환불 신청")
                        .setMessage("교환/환불 신청 하시겠습니까?")
                        .setPositiveButton("확인") { _: DialogInterface, _: Int ->

                        }
                        .setNegativeButton("취소", null)
                        .show()
                }
            }
        }
    }

    class OrderDateViewHolder(val orderDateBinding: ItemOrderDateBinding) :
        OrderViewHolder(orderDateBinding.root) {
        override fun onBind(item: ParentOrderItem) {
            val orderDate = item as OrderDateClass
            orderDateBinding.run {
                val dateFormat1 = SimpleDateFormat(
                    "yyMMdd",
                    Locale.getDefault()
                )
                val dateFormat2 =
                    SimpleDateFormat("yy. M. d", Locale.getDefault())
                val dateData = orderDate.date
                val parseData = dateFormat1.parse(dateData)

                // yy/MM/dd 형식의 Date
                val orderFormatDate = dateFormat2.format(parseData)
                textViewOrderDate.text = orderFormatDate
            }
        }
    }
}