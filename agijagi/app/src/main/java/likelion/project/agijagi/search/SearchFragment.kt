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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentSearchBinding
import likelion.project.agijagi.search.adapter.RecentSearchAdapter
import likelion.project.agijagi.search.adapter.SearchAdapter
import likelion.project.agijagi.search.vm.SearchViewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel by lazy { ViewModelProvider(this)[SearchViewModel::class.java] }

    private lateinit var recentSearchAdapter: RecentSearchAdapter
    private lateinit var searchAdapter: SearchAdapter

    companion object {
        val recentSearchesList = mutableListOf<String>()
    }

    var productNameAndBrandList = listOf<String>()

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

        setToolbarNavigationAction()
        updateSearchListDisplay()
        setupRecyclerViewRecentSearches()
        setupRecyclerViewSearch()
    }

    private fun setToolbarNavigationAction() {
        binding.toolbarSearch.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }
    }

    private fun observeProductListAndFetchData() {
        searchViewModel.productList.observe(viewLifecycleOwner) { productList ->
            productNameAndBrandList = productList
        }

        searchViewModel.getProductNameAndBrand()
    }

    private fun updateSearchListDisplay() {
        binding.run {
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
                            textviewSearchNoSearch.visibility = VISIBLE

                            observeProductListAndFetchData()

                            for (keyword in productNameAndBrandList) {
                                if (keyword.contains(searchText)) {
                                    searchList.add(keyword)
                                    textviewSearchNoSearch.visibility = GONE
                                }
                                if (keyword !in productNameAndBrandList) {
                                    textviewSearchNoSearch.visibility = VISIBLE
                                }
                                searchAdapter.submitList(searchList)
                            }
                        } else {
                            linearlayoutSearchRecentSearches.visibility = VISIBLE
                            recyclerviewSearch.visibility = GONE
                            textviewSearchNoSearch.visibility = GONE
                        }
                    }
                })

                setOnEditorActionListener { _, _, _ ->
                    recentSearchesList.add(text.toString())
                    false
                }
            }
        }
    }

    private fun setupRecyclerViewRecentSearches() {
        binding.recyclerviewSearchRecentSearches.run {
            adapter = recentSearchAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
            (layoutManager as LinearLayoutManager).stackFromEnd = true
        }

        recentSearchAdapter.submitList(recentSearchesList)
    }

    private fun setupRecyclerViewSearch() {
        binding.recyclerviewSearch.run {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
