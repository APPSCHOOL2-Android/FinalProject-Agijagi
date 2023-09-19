package likelion.project.agijagi.category.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import likelion.project.agijagi.R
import likelion.project.agijagi.category.model.CategoryDetailInfoListModel
import likelion.project.agijagi.databinding.ItemCategoryDetailInfoListBinding

class CategoryDetailInfoListAdapter :
    ListAdapter<CategoryDetailInfoListModel, CategoryDetailInfoListAdapter.CategoryListViewHolder>(
        diffUtil
    ) {

    inner class CategoryListViewHolder(val bind: ItemCategoryDetailInfoListBinding) :
        RecyclerView.ViewHolder(bind.root) {

        fun bind(item: CategoryDetailInfoListModel) {
            with(bind) {
                textviewCategoryDetailInfoBrand.text = item.brand
                textviewCategoryDetailInfoName.text = item.name
                textviewCategoryDetailInfoPrice.text = item.price
            }

            // 추후 기성품, 주문 제작 상품 구분 필요
            bind.root.setOnClickListener {
                // 주문 제작 상품일 떄
                it.findNavController()
                    .navigate(R.id.action_categoryDetailInfoListFragment_to_customProductDetailFragment)
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