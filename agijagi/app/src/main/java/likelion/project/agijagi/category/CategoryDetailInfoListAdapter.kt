package likelion.project.agijagi.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import likelion.project.agijagi.databinding.ItemCategoryDetailInfoListBinding
import likelion.project.agijagi.databinding.ItemWishListBinding

class CategoryDetailInfoListAdapter() :
    ListAdapter<CategoryDetailInfoListModel, CategoryDetailInfoListAdapter.CategoryListViewHolder>(diffUtil) {

    inner class CategoryListViewHolder(val bind: ItemCategoryDetailInfoListBinding) :
        RecyclerView.ViewHolder(bind.root) {

        fun bind(item: CategoryDetailInfoListModel) {
            with(bind) {
                textviewCategoryDetailInfoBrand.text = item.brand
                textviewCategoryDetailInfoName.text = item.name
                textviewCategoryDetailInfoPrice.text = item.price
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        val itemCategoryDetailInfoListBinding =
            ItemCategoryDetailInfoListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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