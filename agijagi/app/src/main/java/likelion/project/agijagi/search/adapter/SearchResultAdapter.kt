package likelion.project.agijagi.search.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import likelion.project.agijagi.R
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
                    .placeholder(R.drawable.search_result_default_image)
                    .into(binding.imageviewSearchSearchResultImage)
                textviewSearchSearchResultBrand.text = item.prodBrand
                textviewSearchSearchResultName.text = item.prodName
                textviewSearchSearchResultPrice.text = item.prodPrice
                root.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("prodId", item.prodId)
                    it.findNavController()
                        .navigate(R.id.action_searchResultFragment_to_productDetailFragment, bundle)
                }
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

        return SearchResultViewHolder(itemSearchSearchResultBinding)
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