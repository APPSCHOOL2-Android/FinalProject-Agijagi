package likelion.project.agijagi.buyermypage.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import likelion.project.agijagi.R
import likelion.project.agijagi.buyermypage.ShippingManagementFragment
import likelion.project.agijagi.buyermypage.ShippingManagementFragment.Companion.shippingManagementList
import likelion.project.agijagi.buyermypage.model.ShippingManagementModel
import likelion.project.agijagi.buyermypage.repository.ShippingManagementRepository
import likelion.project.agijagi.databinding.ItemShippingManagementBinding
import likelion.project.agijagi.model.ShippingAddress
import likelion.project.agijagi.purchase.PaymentFragment

class ShippingManagementAdapter(var showCheckBox: Boolean) :
    ListAdapter<ShippingAddress, ShippingManagementAdapter.ShippingManagementViewHolder>(
        diffUtil
    ) {
    private var selectedPosition = RecyclerView.NO_POSITION // 체크박스 선택된 포지션
    private var basicPosition = RecyclerView.NO_POSITION    // 기본배송지 포지션
    private val repository = ShippingManagementRepository()
    inner class ShippingManagementViewHolder(val shippingManagementBinding: ItemShippingManagementBinding) :
        RecyclerView.ViewHolder(shippingManagementBinding.root) {

        fun bind(item: ShippingAddress) {
            shippingManagementBinding.run {
                textViewShippingManagementTitle.text = item.shippingName
                textViewShippingManagementPhone.text = item.phoneNumber
                textViewShippingManagementAddress.text = item.address
                textViewShippingManagementAddress2.text = item.addressDetail
                checkBoxShippingManagementBasic.isChecked = item.shippingAddressChecked
                // 기본배송지 텍스트뷰 표시
                repository.getBasicShippingAddress { basicFieldValue ->
                    if (basicFieldValue == item.shippingAddressId) {
                        textViewShippingMamagementBasic.visibility = View.VISIBLE
                        if(selectedPosition == RecyclerView.NO_POSITION){
                            basicPosition = adapterPosition
                            currentList[basicPosition].shippingAddressChecked = true
                            checkBoxShippingManagementBasic.isChecked = item.shippingAddressChecked
                        }
                    } else {
                        textViewShippingMamagementBasic.visibility = View.GONE
                    }
                }

                Log.d("poooooooooada",showCheckBox.toString())

                // 결제페이지에서 배송지 변경 상태 일 때
                if (showCheckBox) {
                    checkBoxShippingManagementBasic.visibility = View.VISIBLE
                } else {
                    checkBoxShippingManagementBasic.visibility = View.INVISIBLE
                }


                checkBoxShippingManagementBasic.setOnClickListener {
                    handleCheckboxClick(adapterPosition)
                    it.findNavController().popBackStack()
                }

                buttonShippingManagementModify.setOnClickListener {
                    val bundle = Bundle().apply {
                        putString("shippingUpdate", item.shippingAddressId)
                    }
                    it.findNavController()
                        .navigate(R.id.action_shippingManagementFragment_to_shippingUpdateFragment, bundle)
                }

                buttonShippingManagementDelete.setOnClickListener {
                    // db 삭제
                    repository.deleteShippingAddress(shippingManagementList[adapterPosition].shippingAddressId)

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

    private fun handleCheckboxClick(position: Int) {
            // 이전 선택된 체크박스 선택 해제
            if (selectedPosition != RecyclerView.NO_POSITION) {
                currentList[selectedPosition].shippingAddressChecked = false
                notifyItemChanged(selectedPosition)
            }

            // 기본배송지 선택되어있을시 선택 해제
            if(basicPosition != RecyclerView.NO_POSITION) {
                currentList[basicPosition].shippingAddressChecked = false
                notifyItemChanged(basicPosition)
            }

            // 선택한 체크박스 업데이트
            currentList[position].shippingAddressChecked = true
            notifyItemChanged(position)

            selectedPosition = position
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ShippingAddress>() {
            override fun areItemsTheSame(
                oldItem: ShippingAddress,
                newItem: ShippingAddress
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ShippingAddress,
                newItem: ShippingAddress
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}