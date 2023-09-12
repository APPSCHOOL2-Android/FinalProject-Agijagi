package likelion.project.agijagi.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentSearchResultBinding
import likelion.project.agijagi.search.adapter.SearchResultAdapter

class SearchResultFragment : Fragment() {

    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchResultAdapter: SearchResultAdapter

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
        _binding = FragmentSearchResultBinding.inflate(inflater)
        searchResultAdapter = SearchResultAdapter()

        setupToolbar()
        setupRecyclerViewSearchResult()

        return binding.root
    }

    private fun setupToolbar() {
        binding.toolbarSearchResult.run {
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            title = getSearchWord()
        }
    }

    private fun getSearchWord(): String {
        val bundle = arguments
        lateinit var searchWord: String

        if (bundle != null) {
            searchWord = bundle.getString("searchWord")!!
        }

        return searchWord
    }

    private fun setupRecyclerViewSearchResult() {
        binding.recyclerviewSearchResult.run {
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