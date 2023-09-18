package likelion.project.agijagi.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
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
        searchAdapter = SearchAdapter(binding.edittextSearch)

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

    private fun observeProductNameAndBrandList() {
        searchViewModel.productNameAndBrand.observe(viewLifecycleOwner) { productNameAndBrand ->
            productNameAndBrandList = productNameAndBrand
        }

        searchViewModel.getProductNameAndBrand()
    }

    private fun updateSearchListDisplay() {
        binding.run {
            textinputlayoutSearch.run {

            }

            edittextSearch.run {
                doAfterTextChanged {
                    val searchText = it.toString()
                    val searchList = mutableListOf<String>()

                    if (searchText.isNotBlank()) {
                        linearlayoutSearchRecentSearches.visibility = GONE
                        recyclerviewSearch.visibility = VISIBLE
                        textviewSearchNoSearch.visibility = VISIBLE

                        observeProductNameAndBrandList()

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

                setOnEditorActionListener { _, _, _ ->
                    val searchWord = text.toString()
                    recentSearchesList.add(searchWord)
                    val bundle = Bundle()
                    bundle.putString("searchWord", searchWord)
                    edittextSearch.text?.clear()
                    findNavController()
                        .navigate(R.id.action_searchFragment_to_searchResultFragment, bundle)
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
