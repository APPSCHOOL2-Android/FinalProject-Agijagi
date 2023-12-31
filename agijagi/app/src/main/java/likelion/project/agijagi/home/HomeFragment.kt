package likelion.project.agijagi.home

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
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentHomeBinding
import likelion.project.agijagi.home.adapter.BestItemAdapter
import likelion.project.agijagi.model.BuyerModel
import likelion.project.agijagi.model.HomeBestItemModel
import likelion.project.agijagi.model.UserModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val storageRef = Firebase.storage.reference

    lateinit var bestItemAdapter: BestItemAdapter

    private val ref = Firebase.firestore.collection("product")

    private val dataList = mutableListOf<HomeBestItemModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("HomeFragment.onViewCreated()", "uid: ${UserModel.uid}\n name: ${UserModel.name}")
        Log.d(
            "HomeFragment.onViewCreated()",
            "basic: ${BuyerModel.basic} \nnickname: ${BuyerModel.nickname}\n notif_setting: ${BuyerModel.notifSetting}"
        )

        bestItemAdapter = BestItemAdapter()

        setToolbarMenuItem()
        setOrderMadeProductButton()
        setBestItem()
    }

    private fun setToolbarMenuItem() {
        binding.toolbarHome.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_home_search -> {
                    findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
                }

                R.id.menu_home_shopping_list -> {
                    if (UserModel.uid == "") {
                        MainActivity.displayDialogUserNotLogin(
                            requireContext(),
                            findNavController(),
                            R.id.action_homeFragment_to_loginFragment
                        )
                    } else {
                        findNavController().navigate(R.id.action_homeFragment_to_shoppingListFragment)
                    }
                }
            }
            false
        }
    }

    private fun setOrderMadeProductButton() {
        binding.linearlayoutHomeToOrderMadeCategory.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_orderMadeCategoryFragment)
        }
    }

    private fun setRecyclerBestItemData(documents: List<QueryDocumentSnapshot>) {
        for (document in documents) {
            Log.d("HomeFragment.setRecyclerBestItemData()", "document id: ${document.id}")
            try {
                val thumbnailImage = document.data["thumbnail_image"].toString()
                storageRef.child(thumbnailImage).downloadUrl.addOnSuccessListener { uri ->
                    val model = HomeBestItemModel(
                        document.id,
                        document.getBoolean("is_custom"),
                        uri.toString(),
                        document.data["brand"].toString(),
                        document.data["name"].toString(),
                        document.data["price"].toString()
                    )
                    dataList.add(model)
                    setRecyclerviewBestItem(dataList)
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

    private fun setBestItem() {
        // is_custom 을 통해 order made인지 아닌지 분류
        binding.run {
            dataList.clear()

            ref.run {
                get().addOnSuccessListener { documents ->
                    val filteredDocuments =
                        documents.filter { it.getString("state") != "숨김" }
                            .sortedByDescending { it.getLong("sales_quantity") }
                            .take(4)
                    setRecyclerBestItemData(filteredDocuments)
                }.addOnFailureListener {
                    Log.d("FirebaseException", it.toString())
                }
            }
        }
    }

    private fun setRecyclerviewBestItem(list: List<HomeBestItemModel>) {
        binding.recyclerviewHomeBestItem.run {
            layoutManager = GridLayoutManager(context, 2)
            adapter = bestItemAdapter
        }
        bestItemAdapter.submitList(list)
    }

    private fun showTravelData(isLoading: Boolean) {
        if (isLoading) {
            binding.run {
                shimmerFrameLayoutHomeBestItem.startShimmer()
                shimmerFrameLayoutHomeLogo.visibility = View.VISIBLE
                shimmerFrameLayoutHomeBestItem.visibility = View.VISIBLE
                linearlaoutHomeLogo.visibility = View.GONE
                recyclerviewHomeBestItem.visibility = View.GONE
            }
        } else {
            binding.run {
                shimmerFrameLayoutHomeBestItem.stopShimmer()
                shimmerFrameLayoutHomeLogo.visibility = View.GONE
                shimmerFrameLayoutHomeBestItem.visibility = View.GONE
                linearlaoutHomeLogo.visibility = View.VISIBLE
                recyclerviewHomeBestItem.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}