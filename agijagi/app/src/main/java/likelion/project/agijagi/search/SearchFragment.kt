package likelion.project.agijagi.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    lateinit var searchAdapter: SearchAdapter

    companion object {
        val recentSearchesList =
            mutableListOf("그릇", "밥그릇", "컵", "접시", "아기자기", "커스텀 도자기", "custom", "레터링", "Agijagi")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater)
        searchAdapter = SearchAdapter()

        setRecyclerViewRecentSearches()

        return _binding?.root
    }

    private fun setRecyclerViewRecentSearches() {
        _binding?.run {
            recyclerviewSearchRecentSearches.run {
                adapter = searchAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
        }
        searchAdapter.submitList(recentSearchesList)
    }
}
