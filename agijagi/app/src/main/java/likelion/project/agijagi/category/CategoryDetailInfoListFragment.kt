package likelion.project.agijagi.category

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import likelion.project.agijagi.R
import likelion.project.agijagi.category.adapter.CategoryDetailInfoListAdapter
import likelion.project.agijagi.model.CategoryDetailInfoListModel
import likelion.project.agijagi.databinding.FragmentCategoryDetailInfoListBinding

class CategoryDetailInfoListFragment : Fragment() {

    private var _binding: FragmentCategoryDetailInfoListBinding? = null
    private val binding get() = _binding!!

    lateinit var categoryListAdapter: CategoryDetailInfoListAdapter

    private val storageRef = Firebase.storage.reference
    private val ref = Firebase.firestore.collection("product")

    private var getCategory = ""
    private var getIsCustom = 0
    private val dataList = mutableListOf<CategoryDetailInfoListModel>()

    private val categoryList = listOf("Cup", "Bowl", "Plate")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryDetailInfoListBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryListAdapter = CategoryDetailInfoListAdapter()
        getCategory = arguments?.getString("category").toString()
        getIsCustom = arguments?.getInt("is_custom").toString().toInt()
        Log.d("CategoryDetailInfoListFragment.onViewCreated()", "category: ${getCategory}")
        Log.d("CategoryDetailInfoListFragment.onViewCreated()", "iscustom: ${getIsCustom}")
        setToolbarTitle()
        setToolbarMenuItem()
        setCategoryList(getCategory, getIsCustom)
    }

    // 0 == all , 1 == is_custom = true, -1 == is_cusom = false
    private fun setCategoryList(category: String, is_custom: Int) {
        // is_custom 을 통해 order made인지 아닌지 분류
        binding.run {
            dataList.clear()

            ref.run {
                showTravelData(true)
                if (category == "All" && is_custom == 0) {

                    get().addOnSuccessListener { documents ->
                        val filteredDocuments =
                            documents.filter { it.getString("state") != "숨김" }

                        setRecyclerCategoryList(filteredDocuments)
                        showTravelData(false)

                    }.addOnFailureListener {
                        Log.d("FirebaseException", it.toString())
                    }
                } else if (category == "All" && is_custom == 1) {
                    whereEqualTo("is_custom", true)
                        .get()
                        .addOnSuccessListener { documents ->
                            val filteredDocuments =
                                documents.filter { it.getString("state") != "숨김" }

                            setRecyclerCategoryList(filteredDocuments)
                            showTravelData(false)

                        }.addOnFailureListener {
                            Log.d("FirebaseException", it.toString())
                        }
                } else if (category in categoryList && is_custom == -1) {
                    whereEqualTo("category", category)
                        .whereEqualTo("is_custom", false)
                        .get()
                        .addOnSuccessListener { documents ->
                            val filteredDocuments =
                                documents.filter { it.getString("state") != "숨김" }

                            setRecyclerCategoryList(filteredDocuments)
                            showTravelData(false)

                        }.addOnFailureListener {
                            Log.d("FirebaseException", it.toString())
                        }
                } else if (category in categoryList && is_custom == 1) {
                    whereEqualTo("category", category)
                        .whereEqualTo("is_custom", true)
                        .get()
                        .addOnSuccessListener { documents ->
                            val filteredDocuments =
                                documents.filter { it.getString("state") != "숨김" }

                            setRecyclerCategoryList(filteredDocuments)
                            showTravelData(false)

                        }.addOnFailureListener {
                            Log.d("FirebaseException", it.toString())
                        }
                } else {
                    Log.d("CategoryDetailInfoListFragment.setCategoryList()", "else")
                }
            }
        }
    }

    private fun showTravelData(isLoading: Boolean) {
        if (isLoading) {
            binding.run {
                shimmerFrameLayoutCategoryList.startShimmer()
                shimmerFrameLayoutCategoryList.visibility = View.VISIBLE
                recyclerviewCategoryDetailInfoList.visibility = View.GONE
            }
        } else {
            binding.run {
                shimmerFrameLayoutCategoryList.stopShimmer()
                shimmerFrameLayoutCategoryList.visibility = View.GONE
                recyclerviewCategoryDetailInfoList.visibility = View.VISIBLE
            }
        }
    }

    private fun setToolbarTitle() {
        binding.run {
            toolbarCategoryDetailInfoList.run {
                // category 클릭 시 정보를 전달 받아서 title 변경 필요
                title = getCategory
            }
        }
    }

    private fun setToolbarMenuItem() {
        binding.toolbarCategoryDetailInfoList.run {
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_category_shopping_list -> {
                        findNavController().navigate(R.id.action_categoryDetailInfoListFragment_to_shoppingListFragment)
                    }
                }
                false
            }
        }
    }

    private fun setRecyclerviewCategory(list: List<CategoryDetailInfoListModel>) {
        binding.recyclerviewCategoryDetailInfoList.run {
            layoutManager = GridLayoutManager(context, 2)
            adapter = categoryListAdapter
        }
        categoryListAdapter.submitList(list)
    }

    private fun setRecyclerCategoryList(documents: List<QueryDocumentSnapshot>) {
        for (document in documents) {
            Log.d(
                "CategoryDetailInfoListFragment.setRecyclerCategoryList()",
                "document id: ${document.id}"
            )
            try {
                val thumbnailImage = document.data["thumbnail_image"].toString()
                storageRef.child(thumbnailImage).downloadUrl.addOnSuccessListener { uri ->
                    Log.d(
                        "CategoryDetailInfoListFragment.setRecyclerCategoryList()",
                        "All & 0 -> uri ${uri}"
                    )
                    val model = CategoryDetailInfoListModel(
                        document.id,
                        document.getBoolean("is_custom"),
                        uri.toString(),
                        document.data["brand"].toString(),
                        document.data["name"].toString(),
                        document.data["price"].toString()
                    )
                    dataList.add(model)
                    setRecyclerviewCategory(dataList)
                }
            } catch (e: Exception) {
                Log.e(
                    "StorageException",
                    "Error downloading image: ${e.message}",
                    e
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}