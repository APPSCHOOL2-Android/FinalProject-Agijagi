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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentSearchBinding
import likelion.project.agijagi.search.adapter.RecentSearchAdapter
import likelion.project.agijagi.search.adapter.SearchResultAdapter

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    lateinit var recentSearchAdapter: RecentSearchAdapter
    lateinit var searchResultAdapter: SearchResultAdapter

    companion object {
        val recentSearchesList = mutableListOf<String>()
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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recentSearchAdapter = RecentSearchAdapter()
        searchResultAdapter = SearchResultAdapter()

        setToolbarItemAction()
        test()
        setRecyclerViewRecentSearches()
        setRecyclerViewSearchResult()
    }

    private fun setToolbarItemAction() {
        binding.toolbarSearch.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun test() {
        binding.run {
            linearlayoutSearchRecentSearches.visibility = VISIBLE
            recyclerviewSearchSearchResult.visibility = GONE

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
                        if (start == 0 && count == 0) {
                            linearlayoutSearchRecentSearches.visibility = VISIBLE
                            recyclerviewSearchSearchResult.visibility = GONE
                        } else {
                            linearlayoutSearchRecentSearches.visibility = GONE
                            recyclerviewSearchSearchResult.visibility = VISIBLE
                        }
                    }

                    override fun afterTextChanged(p0: Editable?) {

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
