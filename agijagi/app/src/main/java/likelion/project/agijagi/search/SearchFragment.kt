package likelion.project.agijagi.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    lateinit var searchAdapter: SearchAdapter
    lateinit var searchResultAdapter: SearchResultAdapter

    companion object {
        val recentSearchesList =
            mutableListOf("그릇", "밥그릇", "컵", "접시", "아기자기", "커스텀 도자기", "custom", "레터링", "Agijagi")
    }

    val dataList = mutableListOf(
        SearchResultModel(R.drawable.search_result_default_image, "아기자기", "접시", "2억원"),
        SearchResultModel(R.drawable.search_result_default_image, "김자기", "화려한 접시", "2억원"),
        SearchResultModel(R.drawable.search_result_default_image, "agijagi", "그릇", "2억원"),
        SearchResultModel(R.drawable.search_result_default_image, "brand", "아기자기 커스텀 접시", "2억원"),
        SearchResultModel(R.drawable.search_result_default_image, "star", "컵", "2억원"),
        SearchResultModel(R.drawable.search_result_default_image, "김자기", "화려한 그릇", "2억원")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater)
        searchAdapter = SearchAdapter()
        searchResultAdapter = SearchResultAdapter()

        setRecyclerViewRecentSearches()
        setRecyclerViewSearchResult()

        return binding.root
    }

    private fun setRecyclerViewRecentSearches() {
        binding.recyclerviewSearchRecentSearches.run {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        searchAdapter.submitList(recentSearchesList)
    }

    private fun setRecyclerViewSearchResult() {
        binding.recyclerviewSearchSearchResult.run {
            adapter = searchResultAdapter
            layoutManager = GridLayoutManager(context, 2)
        }
        searchResultAdapter.submitList(dataList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
