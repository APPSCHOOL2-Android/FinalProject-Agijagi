package likelion.project.agijagi.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import likelion.project.agijagi.databinding.ItemSearchSearchResultBinding
import likelion.project.agijagi.search.SearchResultModel

class SearchResultAdapter :
    ListAdapter<SearchResultModel, SearchResultAdapter.SearchResultViewHolder>(diffUtil) {

    inner class SearchResultViewHolder(private val binding: ItemSearchSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchResultModel) {

            binding.run {
                Glide.with(itemView)
                    .load(item.prodImage)
                    .into(binding.imageviewSearchSearchResultImage)
                textviewSearchSearchResultBrand.text = item.prodBrand
                textviewSearchSearchResultName.text = item.prodName
                textviewSearchSearchResultPrice.text = item.prodPrice
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchResultViewHolder {
        val itemSearchSearchResultBinding = ItemSearchSearchResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val viewHolder = SearchResultViewHolder(itemSearchSearchResultBinding)

        return viewHolder
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<SearchResultModel>() {
            override fun areItemsTheSame(
                oldItem: SearchResultModel,
                newItem: SearchResultModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: SearchResultModel,
                newItem: SearchResultModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}