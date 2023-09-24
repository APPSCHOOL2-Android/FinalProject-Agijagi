package likelion.project.agijagi.sellermypage

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentOrderManagementBinding
import likelion.project.agijagi.model.UserModel
import likelion.project.agijagi.sellermypage.adapter.OrderManagementAdapter
import likelion.project.agijagi.sellermypage.model.OrderManagementModel

class OrderManagementFragment : Fragment() {

    private var _binding: FragmentOrderManagementBinding? = null
    private val binding get() = _binding!!
    lateinit var orderManagementAdapter: OrderManagementAdapter

    private val dataSet = arrayListOf<OrderManagementModel>()

    private val db = Firebase.firestore
    private val storageRef = Firebase.storage.reference

    private lateinit var bundle: Bundle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderManagementBinding.inflate(inflater)

        orderManagementAdapter = OrderManagementAdapter(itemClick = { item ->
            bundle = Bundle().apply {
                putParcelable("orderDataSet", item)
            }
            findNavController().navigate(
                R.id.action_orderManagementFragment_to_orderManagementDetailFragment, bundle
            )
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarItemAction()

        runBlocking {
            getData()
        }
    }

    private suspend fun getData() {
        // 쉬머
        showSampleData(true)
        CoroutineScope(Dispatchers.IO).launch {
            // product -> seller.roleId같은거
            val sellerProduct =
                db.collection("product").whereEqualTo("seller_id", UserModel.roleId).get().await()
            val orderProduct = db.collection("order").get().await()

            dataSet.clear()
            // order에서 sellerProduct에 있는 prodId와 같은걸 가져옴
            sellerProduct.forEach { sellerProduct ->
                orderProduct.forEach { orderProduct ->
                    val productId = db.collection("order").document(orderProduct.id)
                        .collection("prod_Info").get().await()
                    productId.forEach { productId ->
                        // order sample -> RXRgApiyoL4SjEcxXZXO
                        if (sellerProduct.id == productId["prodInfoId"]) {
                            val thumbnail = sellerProduct["thumbnail_image"].toString()
                            val uri = storageRef.child(thumbnail).downloadUrl.await()

                            Log.d("tttt", sellerProduct.id)
                            Log.d("tttt", orderProduct.data["buyerId"].toString())

                            var userName = ""
                            val buyerUserId = db.collection("user").get().await()
                            buyerUserId.forEach {
                                if (it["role_id"] == orderProduct.data["buyerId"].toString()) {
                                    userName = it["name"].toString()
                                }
                            }

                            dataSet.add(
                                OrderManagementModel(
                                    sellerProduct["brand"].toString(),
                                    sellerProduct["name"].toString(),
                                    sellerProduct["price"].toString(),
                                    uri.toString(),
                                    sellerProduct["update_date"].toString(), //date
                                    orderProduct.id, // orderId
                                    sellerProduct.id, // prodInfoId
                                    userName, // userName
                                    orderProduct["totalPrice"].toString()
                                )
                            )
                        }
                    }
                }
            }
            withContext(Dispatchers.Main) {
                dataSet.sortByDescending { it.date }
                setRecyclerView()
                showSampleData(false)
            }
        }
    }

    private fun showSampleData(isLoading: Boolean) {
        if (isLoading) {
            binding.recyclerviewOrderManagementShimmer.startShimmer()
            binding.recyclerviewOrderManagementShimmer.visibility = View.VISIBLE
            binding.recyclerviewOrderManagement.visibility = View.GONE
        } else {
            binding.recyclerviewOrderManagementShimmer.stopShimmer()
            binding.recyclerviewOrderManagementShimmer.visibility = View.GONE
            binding.recyclerviewOrderManagement.visibility = View.VISIBLE
        }
    }

    private fun setRecyclerView() {
        binding.run {
            recyclerviewOrderManagement.run {
                layoutManager = LinearLayoutManager(context)
                adapter = orderManagementAdapter
            }
            orderManagementAdapter.submitList(dataSet)
        }
    }

    private fun setToolbarItemAction() {
        binding.toolbarOrderManagement.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}