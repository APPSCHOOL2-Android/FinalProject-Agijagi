package likelion.project.agijagi.buyermypage.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import likelion.project.agijagi.R
import likelion.project.agijagi.buyermypage.ShippingManagementFragment.Companion.shippingManagementList
import likelion.project.agijagi.buyermypage.model.ShippingManagementModel
import likelion.project.agijagi.buyermypage.repository.ShippingManagementRepository
import likelion.project.agijagi.databinding.ItemShippingManagementBinding

class ShippingManagementAdapter :
    ListAdapter<ShippingManagementModel, ShippingManagementAdapter.ShippingManagementViewHolder>(
        diffUtil
    ) {

    inner class ShippingManagementViewHolder(val shippingManagementBinding: ItemShippingManagementBinding) :
        RecyclerView.ViewHolder(shippingManagementBinding.root) {

        fun bind(item: ShippingManagementModel) {
            shippingManagementBinding.run {
                val repository = ShippingManagementRepository()
                textViewShippingManagementTitle.text = item.title
                textViewShippingManagementPhone.text = item.recipientPhone
                textViewShippingManagementAddress.text = item.address
                textViewShippingManagementAddress2.text = item.addressDetail

                // 기본배송지 텍스트뷰 표시
                repository.getBasicShippingAddress { basicFieldValue ->
                    if (basicFieldValue == item.uid) {
                        textViewShippingMamagementBasic.visibility = View.VISIBLE
                    } else {
                        textViewShippingMamagementBasic.visibility = View.GONE
                    }
                }

                buttonShippingManagementModify.setOnClickListener {
                    val bundle = Bundle().apply {
                        putString("shippingUpdate", item.uid)
                    }
                    it.findNavController()
                        .navigate(R.id.action_shippingManagementFragment_to_shippingUpdateFragment, bundle)
                }

                buttonShippingManagementDelete.setOnClickListener {
                    // db 삭제
                    repository.deleteShippingAddress(shippingManagementList[adapterPosition].uid)

                    shippingManagementList.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                }
            }
        }

        init {
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShippingManagementViewHolder {
        val itemShippingManagementBinding =
            ItemShippingManagementBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
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