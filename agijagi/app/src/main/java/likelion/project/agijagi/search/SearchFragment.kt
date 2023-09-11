package likelion.project.agijagi.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentSearchBinding
import likelion.project.agijagi.search.adapter.RecentSearchAdapter
import likelion.project.agijagi.search.adapter.SearchAdapter
import java.util.Locale

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var recentSearchAdapter: RecentSearchAdapter
    private lateinit var searchAdapter: SearchAdapter

    companion object {
        val recentSearchesList = mutableListOf<String>()
    }

//    val dataList = mutableListOf(
//        SearchResultModel(R.drawable.search_result_default_image, "아기자기", "접시", "2억원"),
//        SearchResultModel(R.drawable.search_result_default_image, "김자기", "화려한 접시", "2억원"),
//        SearchResultModel(R.drawable.search_result_default_image, "agijagi", "그릇", "2억원"),
//        SearchResultModel(R.drawable.search_result_default_image, "brand", "아기자기 커스텀 접시", "2억원"),
//        SearchResultModel(R.drawable.search_result_default_image, "star", "컵", "2억원"),
//        SearchResultModel(R.drawable.search_result_default_image, "김자기", "화려한 그릇", "2억원")
//    )

    val productList = mutableListOf(
        "아기자기", "김자기", "감자기", "brand name", "agijagi", "jagi", "JAGIJAGI", "aa"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recentSearchAdapter = RecentSearchAdapter()
        searchAdapter = SearchAdapter()

        setToolbarItemAction()
        test()
        setRecyclerViewRecentSearches()
        setRecyclerViewSearch()
    }

    private fun setToolbarItemAction() {
        binding.toolbarSearch.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }
    }

    private fun test() {
        binding.run {
//            linearlayoutSearchRecentSearches.visibility = VISIBLE
//            recyclerviewSearch.visibility = GONE

            textinputlayoutSearch.run {

            }

            edittextSearch.run {
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        p0: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        p0: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {

                    }

                    override fun afterTextChanged(editable: Editable?) {
                        val searchText = editable.toString()
                        val searchList = mutableListOf<String>()

                        if (searchText.isNotBlank()) {
                            linearlayoutSearchRecentSearches.visibility = GONE
                            recyclerviewSearch.visibility = VISIBLE

                            for (keyword in productList) {
                                if (keyword.contains(searchText)) {
                                    searchList.add(keyword)
                                }
                                searchAdapter.submitList(searchList)
                            }
                        } else {
                            linearlayoutSearchRecentSearches.visibility = VISIBLE
                            recyclerviewSearch.visibility = GONE
                        }
                    }
                })

                setOnEditorActionListener { textView, i, keyEvent ->
                    recentSearchesList.add(text.toString())
                    false
                }
            }
        }
    }

    private fun setRecyclerViewRecentSearches() {
        binding.recyclerviewSearchRecentSearches.run {
            adapter = recentSearchAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
            (layoutManager as LinearLayoutManager).stackFromEnd = true
        }

        recentSearchAdapter.submitList(recentSearchesList)
    }

    private fun setRecyclerViewSearch() {
        binding.recyclerviewSearch.run {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(context)
        }
        //searchAdapter.submitList(dataList2)
    }

//    private fun setRecyclerViewSearchResult() {
//        binding.recyclerviewSearchSearchResult.run {
//            adapter = searchResultAdapter
//            layoutManager = GridLayoutManager(context, 2)
//        }
//        searchResultAdapter.submitList(dataList)
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
