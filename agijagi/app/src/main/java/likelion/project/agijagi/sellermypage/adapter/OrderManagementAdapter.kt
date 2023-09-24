package likelion.project.agijagi.sellermypage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.ItemOrderManagementBinding
import likelion.project.agijagi.sellermypage.model.OrderManagementModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

class OrderManagementAdapter(val itemClick: (OrderManagementModel) -> Unit) :
    ListAdapter<OrderManagementModel, OrderManagementAdapter.OrderManagementHolder>(diffUtil) {

    inner class OrderManagementHolder(val bind: ItemOrderManagementBinding) :
        RecyclerView.ViewHolder(bind.root) {
        fun bind(item: OrderManagementModel) {
            with(bind) {
                val dec = DecimalFormat("#,###")
                val dateFormat = SimpleDateFormat("yyMMddHHmmssSSS", Locale.getDefault())
                val dateFormat2 = SimpleDateFormat("yy.MM.dd", Locale.getDefault())

                val dateData = dateFormat.parse(item.date)
                val dateData2 = dateFormat2.format(dateData)

                textviewOrderManagementItemBuyerName.text = item.userName
                textviewOrderManagementItemName.text = item.name
                textviewOrderManagementItemPrice.text = "${dec.format(item.totalPrice.toLong())}원"
                textviewOrderManagementDate.text = dateData2


                Glide.with(itemView)
                    .load(item.thumbnailImage)
                    .placeholder(R.drawable.shopping_list_no_item_logo)
                    .into(bind.imageviewOrderManagementItem)
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