package likelion.project.agijagi.wishlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.databinding.FragmentWishListBinding
import likelion.project.agijagi.model.UserModel

class WishListFragment : Fragment() {

    private var _binding: FragmentWishListBinding? = null
    private val binding get() = _binding!!

    lateinit var mainActivity: MainActivity
    lateinit var listAdapter: WishListAdapter

    private val wishListData = arrayListOf<String>()

    private val db = Firebase.firestore
    private val storageRef = Firebase.storage.reference

    companion object {
        val dataSet = mutableListOf<WishListModel>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishListBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        listAdapter = WishListAdapter(itemClick = { item ->
            // prodid 받아서  iscustom 상태에 따라 분기
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            runBlocking {
                getData()
            }
        }
    }

    private suspend fun getData() {
        // 데이터 가져오기전에 shimmer
        showSampleData(true)
        // buyer컬렉션-> userroleid문서->  wish컬렉션에서 데이터 가져와서
        CoroutineScope(Dispatchers.IO).launch {
            val buyer =
                db.collection("buyer").document(UserModel.roleId)
                    .collection("wish").get().await()
            wishListData.clear()
            for (document in buyer) {
                wishListData.add(document.id)
            }
            // 이게 끝나고 밑에 실행
            wishListData.forEach { prodId ->
                val document = db.collection("product").document(prodId).get().await()
                val thumbnailImage = document["thumbnail_image"].toString()
                val uri = storageRef.child(thumbnailImage).downloadUrl.await()

                dataSet.add(
                    WishListModel(
                        document["brand"].toString(),
                        document["name"].toString(),
                        document["price"].toString(),
                        uri.toString(),
                        prodId,
                        true
                    )
                )
            }

            withContext(Dispatchers.Main) {
                setRecyclerView()
                showSampleData(false)
            }
        }
    }

    private fun setRecyclerView() {
        binding.run {
            recyclerviewWishList.run {
                layoutManager = GridLayoutManager(requireContext(), 2)
                adapter = listAdapter
            }
            listAdapter.submitList(dataSet)

            // dataList가 비어있을 때 보이도록
            if (dataSet.isEmpty()) {
                binding.recyclerviewWishListEmpty.visibility = View.VISIBLE
            } else {
                binding.recyclerviewWishListEmpty.visibility = View.GONE
            }
        }
    }

    private fun showSampleData(isLoading: Boolean) {
        if (isLoading) {
            binding.recyclerviewWishListShimmer.startShimmer()
            binding.recyclerviewWishListShimmer.visibility = View.VISIBLE
            binding.recyclerviewWishList.visibility = View.GONE
        } else {
            binding.recyclerviewWishListShimmer.stopShimmer()
            binding.recyclerviewWishListShimmer.visibility = View.GONE
            binding.recyclerviewWishList.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}