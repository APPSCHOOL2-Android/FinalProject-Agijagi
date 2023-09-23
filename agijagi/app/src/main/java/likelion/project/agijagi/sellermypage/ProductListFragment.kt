package likelion.project.agijagi.sellermypage

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentProductListBinding
import likelion.project.agijagi.model.ProductModel
import likelion.project.agijagi.model.UserModel
import likelion.project.agijagi.sellermypage.adapter.ProductListAdapter
import java.text.SimpleDateFormat
import java.util.Locale

class ProductListFragment : Fragment() {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!
    lateinit var productListAdapter: ProductListAdapter

    val dataList = arrayListOf<ProductModel>().apply {
    }

    val db = FirebaseFirestore.getInstance()
    val roleId = UserModel.roleId
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productListAdapter = ProductListAdapter(requireContext())

        getProductData()
        binding.run {
            setToolbarItemAction()

            recyclerviewProductList.run {
                adapter = productListAdapter
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(MarginItemDecoration(40))
            }
        }
    }

    // 데이터 로딩 shimmer 라이브러리 사용
    private fun showSampleData(isLoading: Boolean) {
        if (isLoading) {
            binding.shimmerProductList.startShimmer()
            binding.shimmerProductList.visibility = View.VISIBLE
            binding.recyclerviewProductList.visibility = View.GONE
        } else {
            binding.shimmerProductList.stopShimmer()
            binding.shimmerProductList.visibility = View.GONE
            binding.recyclerviewProductList.visibility = View.VISIBLE
        }
    }

    // 배송지 데이터 불러오기
    private fun getProductData() {
        CoroutineScope(Dispatchers.IO).launch {
            showSampleData(true)
            // 리스트 초기화
            dataList.clear()

            val querySnapshot = db.collection("product").whereEqualTo("seller_id", roleId)
                .get().await()
            for (document in querySnapshot) {
                dataList.add(
                    ProductModel(
                        document.id,
                        document.getString("brand") ?: "",
                        document.getString("category") ?: "",
                        document.get("customOptionInfo") as? HashMap<String, String> ?: HashMap(),
                        document.getString("detail") ?: "",
                        document.get("floorPlan") as? ArrayList<String> ?: ArrayList(),
                        document.get("image") as? ArrayList<String> ?: ArrayList(),
                        document.getBoolean("isCustom") ?: false,
                        document.getString("name") ?: "",
                        document.getString("state") ?: "",
                        document.getString("price") ?: "",
                        document.getLong("salesQuantity") ?: 0,
                        document.getLong("shoppingQuantity") ?: 0,
                        document.getString("sellerId") ?: "",
                        document.getString("thumbnail_image") ?: "",
                        document.getString("update_date") ?: ""
                    )
                )
            }
            // 날짜 등록순 정렬
            dataList.sortByDescending {
                parseUpdateDate(it.updateDate)
            }
            // UI
            withContext(Dispatchers.Main){
                // dataList가 비어있을 때 보이도록
                if (dataList.isEmpty()) {
                    binding.textViewProductListEmpty.visibility = View.VISIBLE
                } else {
                    binding.textViewProductListEmpty.visibility = View.GONE
                }

                productListAdapter.submitList(dataList)
                delay(1500)
                showSampleData(false)
            }
        }
    }

    // 문자열 date를 날짜로 파싱
    private fun parseUpdateDate(updateDate: String): Long {
        val dateFormat = SimpleDateFormat("yyMMddhhmmsssss", Locale.getDefault())
        val date = dateFormat.parse(updateDate)
        return date?.time ?: 0L
    }

    // 상품리스트에서 상품상태 변경시 DB업데이트
    fun updateProductState(productModel: ProductModel, productState: String) {
        db.collection("product")
            .document(productModel.productId)
            .update("state", productState)
    }

    private fun setToolbarItemAction() {
        binding.toolbarProductList.run {
            setNavigationOnClickListener {
                findNavController().navigate(R.id.action_productListFragment_to_sellerMypageFragment)
            }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_product_list_add -> {
                        findNavController().navigate(R.id.action_productListFragment_to_productAddFragment)
                    }
                }
                false
            }
        }
    }

    inner class MarginItemDecoration(private val spaceSize: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val itemCount = state.itemCount

            // px -> dp 변환
            val transformationDP = resources.displayMetrics.density
            val size = (spaceSize * transformationDP).toInt()

            with(outRect) {
                bottom = if (position == itemCount - 1) size else 0
            }
        }
    }

}