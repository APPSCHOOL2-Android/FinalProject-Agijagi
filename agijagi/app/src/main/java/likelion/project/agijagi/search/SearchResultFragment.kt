package likelion.project.agijagi.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import likelion.project.agijagi.databinding.FragmentSearchResultBinding
import likelion.project.agijagi.search.adapter.SearchResultAdapter

class SearchResultFragment : Fragment() {

    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

    private val storageRef = Firebase.storage.reference
    private val ref = Firebase.firestore.collection("product")

    private lateinit var searchResultAdapter: SearchResultAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultBinding.inflate(inflater)
        searchResultAdapter = SearchResultAdapter()

        getProductInfoBasedOnSearchResults()
        setupToolbar()

        return binding.root
    }

    private fun getProductInfoBasedOnSearchResults() {
        val searchResultList = mutableListOf<SearchResultModel>()
        ref.run {
            whereEqualTo("brand", getSearchWord())
                .get()
                .addOnSuccessListener {
                    for (document in it) {
                        val thumbnailImage = document.data["thumbnail_image"].toString()
                        storageRef.child(thumbnailImage).downloadUrl.addOnSuccessListener { uri ->
                            val model = SearchResultModel(
                                document.id,
                                uri.toString(),
                                document.data["brand"].toString(),
                                document.data["name"].toString(),
                                document.data["price"].toString()
                            )
                            searchResultList.add(model)
                            setupRecyclerViewSearchResult(searchResultList)
                        }
                    }
                }

            whereEqualTo("name", getSearchWord())
                .get()
                .addOnSuccessListener {
                    for (document in it) {
                        val thumbnailImage = document.data["thumbnail_image"].toString()
                        storageRef.child(thumbnailImage).downloadUrl.addOnSuccessListener { uri ->
                            val model = SearchResultModel(
                                document.id,
                                uri.toString(),
                                document.data["brand"].toString(),
                                document.data["name"].toString(),
                                document.data["price"].toString()
                            )
                            searchResultList.add(model)
                            setupRecyclerViewSearchResult(searchResultList)
                        }
                    }
                }
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

    private fun setupToolbar() {
        binding.toolbarSearchResult.run {
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            title = getSearchWord()
        }
    }

    private fun setupRecyclerViewSearchResult(list: List<SearchResultModel>) {
        binding.recyclerviewSearchResult.run {
            adapter = searchResultAdapter
            layoutManager = GridLayoutManager(context, 2)
        }
        searchResultAdapter.submitList(list)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}