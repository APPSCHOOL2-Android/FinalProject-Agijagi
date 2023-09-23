package likelion.project.agijagi.home.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.ItemHomeBestItemBinding
import likelion.project.agijagi.model.HomeBestItemModel

class BestItemAdapter :
    ListAdapter<HomeBestItemModel, BestItemAdapter.BestItemViewHolder>(
        diffUtil
    ) {

    inner class BestItemViewHolder(val bind: ItemHomeBestItemBinding) :
        RecyclerView.ViewHolder(bind.root) {

        fun bind(item: HomeBestItemModel) {
            with(bind) {
                Glide.with(itemView)
                    .load(item.thumbnail)
                    .placeholder(R.drawable.search_result_default_image)
                    .into(bind.imageviewHomeBestItemPhoto)

                textviewHomeBestItemBrand.text = item.brand
                textviewHomeBestItemName.text = item.name
                textviewHomeBestItemPrice.text = item.price
            }

            // 추후 기성품, 주문 제작 상품 구분 필요
            bind.root.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("prodId", item.prodId)
                if (!item.prodisCustom!!) {
                    it.findNavController()
                        .navigate(R.id.action_homeFragment_to_productDetailFragment, bundle)
                } else {
                    it.findNavController()
                        .navigate(R.id.action_homeFragment_to_customProductDetailFragment, bundle)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestItemViewHolder {
        val itemHomeBestItemBinding =
            ItemHomeBestItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        val viewHolder = BestItemViewHolder(itemHomeBestItemBinding)

        return viewHolder
    }

    override fun onBindViewHolder(holder: BestItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<HomeBestItemModel>() {
            override fun areItemsTheSame(
                oldItem: HomeBestItemModel,
                newItem: HomeBestItemModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: HomeBestItemModel,
                newItem: HomeBestItemModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}