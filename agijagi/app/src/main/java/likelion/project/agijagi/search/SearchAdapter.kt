package likelion.project.agijagi.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import likelion.project.agijagi.databinding.ItemSearchRecentSearchesBinding
import likelion.project.agijagi.search.SearchFragment.Companion.recentSearchesList

class SearchAdapter : ListAdapter<String, SearchAdapter.SearchViewHolder>(diffUtil) {

    inner class SearchViewHolder(private val binding: ItemSearchRecentSearchesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recentSearch: String) {
            binding.run {
                textviewRecentSearch.text = recentSearch
                imageButtonDelete.setOnClickListener {
                    recentSearchesList.removeAt(adapterPosition)
                    // notifyDataSetChanged() 사용 하지 않는 코드로 수정 필요
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchRecentSearchesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

}