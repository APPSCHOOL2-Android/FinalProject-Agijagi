package likelion.project.agijagi.order

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.databinding.FragmentOrderBinding
import likelion.project.agijagi.model.UserModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

class OrderFragment : Fragment() {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!
    lateinit var mainActivity: MainActivity
    lateinit var orderAdapter: OrderAdapter

    private val db = FirebaseFirestore.getInstance()
    private val storageRef = Firebase.storage.reference
    val roleId = UserModel.roleId
    private val orderList = arrayListOf<ParentOrderItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderAdapter = OrderAdapter()
        getOrderData()
    }

    private fun setRecyclerView() {
        binding.run {
            recyclerviewOrder.run {
                adapter = orderAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }
            orderAdapter.submitList(orderList)
        }
    }

    private fun showSampleData(isLoading: Boolean) {
        if (isLoading) {
            binding.shimmerOrderList.startShimmer()
            binding.shimmerOrderList.visibility = View.VISIBLE
            binding.recyclerviewOrder.visibility = View.GONE
        } else {
            binding.shimmerOrderList.stopShimmer()
            binding.shimmerOrderList.visibility = View.GONE
            binding.recyclerviewOrder.visibility = View.VISIBLE
        }
    }

    private fun getOrderData() {
        showSampleData(true)
        // 리스트 초기화
        orderList.clear()

        val orderDateList = arrayListOf<ParentOrderItem>()
        val orderDataList = arrayListOf<ParentOrderItem>()

        db.collection("order").whereEqualTo("buyerId", roleId)
            .get().addOnSuccessListener { orderQuerySnapshot ->

                for (document in orderQuerySnapshot) {
                    var orderState = ""
                    var orderThumbnailImage = ""
                    var orderBrand = ""
                    var orderName = ""
                    var orderCount = 0L
                    var orderPrice = ""
                    var orderDate = ""
                    var orderSellerId = ""

                    orderDate = document.getString("date") ?: ""
                    orderState = document.getString("state") ?: ""

                    val orderId = document.id
                    db.collection("order").document(orderId).collection("prod_Info")
                        .get()
                        .addOnSuccessListener { prodInfoQuerySnapshot ->

                            for (prodInfoDoc in prodInfoQuerySnapshot) {
                                //  주문갯수(prod_Info), 총가격(prod_Info)
                                val dec = DecimalFormat("#,###")
                                val price = prodInfoDoc.getString("price") ?: ""
                                val priceFormatting =
                                    "${dec.format(price.toLong().plus(3000L))}원"
                                orderCount = prodInfoDoc.getLong("count") ?: 0
                                orderPrice = priceFormatting

                                val prodInfoId = prodInfoDoc.getString("prodInfoId") ?: ""
                                db.collection("product").document(prodInfoId)
                                    .get()
                                    .addOnSuccessListener { productQuerySnapshot ->

                                        //썸네일이미지(product), 브랜드(product) , 제품명(product)
                                        orderBrand = productQuerySnapshot["brand"].toString()
                                        orderName = productQuerySnapshot["name"].toString()
                                        orderSellerId = productQuerySnapshot["seller_id"].toString()
                                        val thumbnailImage =
                                            productQuerySnapshot["thumbnail_image"].toString()

                                        storageRef.child(thumbnailImage).downloadUrl.addOnSuccessListener { uriDoc ->

                                            orderThumbnailImage = uriDoc.toString()

                                            val dateFormat1 = SimpleDateFormat(
                                                "yyMMddHHmmssSSS",
                                                Locale.getDefault()
                                            )
                                            val dateFormat2 =
                                                SimpleDateFormat("yyMMdd", Locale.getDefault())
                                            val dateData = orderDate
                                            val parseData = dateFormat1.parse(dateData)
                                            // yyMMdd 형식의 Date
                                            val orderFormatDate = dateFormat2.format(parseData)

                                            val orderDateItem: ParentOrderItem =
                                                OrderDateClass(date = orderFormatDate)
                                            val orderDataItem: ParentOrderItem = OrderDataClass(
                                                state = orderState,
                                                thumbnailImage = orderThumbnailImage,
                                                brand = orderBrand,
                                                name = orderName,
                                                count = orderCount,
                                                price = orderPrice,
                                                date = orderFormatDate,
                                                buyerId = roleId,
                                                sellerId = orderSellerId
                                            )
                                            orderDateList.add(orderDateItem)
                                            orderDataList.add(orderDataItem)

                                            orderDateList.sortByDescending {
                                                (it as? OrderDateClass)?.date
                                            }
                                            orderDataList.sortByDescending {
                                                (it as? OrderDataClass)?.date
                                            }

                                            orderList.clear()
                                            for (idx in 0 until orderDateList.size) {
                                                if (orderList.isEmpty()) {
                                                    orderList.add(orderDateList[idx])
                                                    orderList.add(orderDataList[idx])
                                                } else {
                                                    val lastOrderDate =
                                                        (orderDataList[idx - 1] as? OrderDataClass)?.date
                                                    val orderDataDate =
                                                        (orderDataList[idx] as? OrderDataClass)?.date
                                                    if (lastOrderDate == orderDataDate) {
                                                        orderList.add(orderDataList[idx])
                                                    } else {
                                                        orderList.add(orderDateList[idx])
                                                        orderList.add(orderDataList[idx])
                                                    }
                                                }
                                            }
                                            if (orderList.isEmpty()) {
                                                binding.textViewOrderNullData.visibility =
                                                    View.VISIBLE
                                            } else {
                                                binding.textViewOrderNullData.visibility = View.GONE
                                            }
                                            setRecyclerView()
                                            showSampleData(false)
                                        }
                                    }
                            }
                        }
                }
                binding.recyclerviewOrder.run {
                    addItemDecoration(MarginItemDecoration(40))
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

// 멀티뷰타입을 이용할 interface
interface ParentOrderItem {
    val viewType: Int
}

// 배송상태(order 필드), 썸네일이미지(product), 브랜드(product) , 제품명(product), 주문갯수(prod_Info), 총가격(prod_Info), 주문날짜(order 필드)
data class OrderDataClass(
    override val viewType: Int = 0,
    var state: String,
    var thumbnailImage: String,
    var brand: String,
    var name: String,
    var count: Long,
    var price: String,
    var date: String,
    var buyerId: String,
    var sellerId: String
) : ParentOrderItem

data class OrderDateClass(
    override val viewType: Int = 1,
    var date: String
) : ParentOrderItem