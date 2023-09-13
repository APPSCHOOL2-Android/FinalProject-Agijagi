package likelion.project.agijagi.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import likelion.project.agijagi.databinding.ItemSearchRecentSearchesBinding
import likelion.project.agijagi.search.SearchFragment.Companion.recentSearchesList

class RecentSearchAdapter : ListAdapter<String, RecentSearchAdapter.RecentSearchViewHolder>(diffUtil) {

    inner class RecentSearchViewHolder(private val binding: ItemSearchRecentSearchesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recentSearch: String) {
            binding.run {
                textviewRecentSearch.text = recentSearch
                imageButtonDelete.setOnClickListener {
                    recentSearchesList.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSearchViewHolder {
        val binding = ItemSearchRecentSearchesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return RecentSearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentSearchViewHolder, position: Int) {
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