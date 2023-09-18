package likelion.project.agijagi.search.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.ItemSearchBinding
import likelion.project.agijagi.search.SearchFragment.Companion.recentSearchesList

class SearchAdapter(val editTextSearch: EditText) : ListAdapter<String, SearchAdapter.SearchViewHolder>(diffUtil) {

    inner class SearchViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(search: String) {
            binding.run {
                textviewSearch.text = search
                root.setOnClickListener {
                    val searchWord = textviewSearch.text.toString()
                    recentSearchesList.add(searchWord)
                    val bundle = Bundle()
                    bundle.putString("searchWord", searchWord)
                    it.findNavController()
                        .navigate(R.id.action_searchFragment_to_searchResultFragment, bundle)

                    editTextSearch.text.clear()
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchBinding.inflate(
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