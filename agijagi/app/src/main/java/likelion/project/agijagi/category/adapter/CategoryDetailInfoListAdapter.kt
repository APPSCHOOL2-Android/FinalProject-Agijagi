package likelion.project.agijagi.category.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import likelion.project.agijagi.R
import likelion.project.agijagi.model.CategoryDetailInfoListModel
import likelion.project.agijagi.databinding.ItemCategoryDetailInfoListBinding
import java.text.DecimalFormat

class CategoryDetailInfoListAdapter :
    ListAdapter<CategoryDetailInfoListModel, CategoryDetailInfoListAdapter.CategoryListViewHolder>(
        diffUtil
    ) {

    inner class CategoryListViewHolder(val bind: ItemCategoryDetailInfoListBinding) :
        RecyclerView.ViewHolder(bind.root) {

        fun bind(item: CategoryDetailInfoListModel) {
            with(bind) {
                val dec = DecimalFormat("#,###")

                Glide.with(itemView)
                    .load(item.thumbnail)
                    .placeholder(R.drawable.category_list_item_default_image)
                    .into(bind.imageviewCategoryDetailInfoListPhoto)

                textviewCategoryDetailInfoBrand.text = item.brand
                textviewCategoryDetailInfoName.text = item.name
                textviewCategoryDetailInfoPrice.text = "${dec.format(item.price.toLong())}원"
            }

            // 추후 기성품, 주문 제작 상품 구분 필요
            bind.root.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("prodId", item.prodId)
                if (!item.prodisCustom!!) {
                    it.findNavController()
                        .navigate(R.id.action_categoryDetailInfoListFragment_to_productDetailFragment, bundle)
                } else {
                    it.findNavController()
                        .navigate(R.id.action_categoryDetailInfoListFragment_to_customProductDetailFragment, bundle)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        val itemCategoryDetailInfoListBinding =
            ItemCategoryDetailInfoListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        val viewHolder = CategoryListViewHolder(itemCategoryDetailInfoListBinding)

        return viewHolder
    }

    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<CategoryDetailInfoListModel>() {
            override fun areItemsTheSame(
                oldItem: CategoryDetailInfoListModel,
                newItem: CategoryDetailInfoListModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: CategoryDetailInfoListModel,
                newItem: CategoryDetailInfoListModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}