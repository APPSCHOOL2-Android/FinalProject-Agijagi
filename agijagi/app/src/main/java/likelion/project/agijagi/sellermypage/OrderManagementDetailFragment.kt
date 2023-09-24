package likelion.project.agijagi.sellermypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.databinding.FragmentOrderManagementDetailBinding
import likelion.project.agijagi.sellermypage.model.OrderManagementModel
import java.text.DecimalFormat


class OrderManagementDetailFragment : Fragment() {

    private var _binding: FragmentOrderManagementDetailBinding? = null
    private val binding get() = _binding!!
    lateinit var mainActivity: MainActivity

    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderManagementDetailBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments?.getParcelable<OrderManagementModel>("orderDataSet") != null) {
            val item = arguments?.getParcelable<OrderManagementModel>("orderDataSet")
            val orderId = item?.orderId.toString()
            val productId = item?.productId.toString()
            val userName = item?.userName.toString()

            CoroutineScope(Dispatchers.IO).launch {
                showSampleData(true)
                val productInfo = db.collection("product").document(productId).get().await()
                val orderData = db.collection("order").document(orderId).get().await()
                val orderProductInfo = db.collection("order").document(orderId)
                    .collection("prod_Info").document(orderData["date"].toString()).get().await()

                val address = orderData["shippingAddress"] as HashMap<String, String>

                withContext(Dispatchers.Main) {
                    showSampleData(false)
                    binding.run {
                        val dec = DecimalFormat("#,###")

                        textViewOrderDetailNumber.text = orderData["orderNum"].toString()
                        textViewOrderDetailName.text = productInfo["name"].toString()
                        textViewOrderDetailOption.text = orderProductInfo["option"].toString()
                        textViewOrderDetailPrice.text =
                            "${dec.format(orderData["totalPrice"].toString().toLong())}원"

                        textViewOrderDetailBuyerName.text = userName
                        textViewOrderDetailBuyerAddress.text =
                            address["address"] + " " + address["address_detail"]
                    }
                }
            }
            setToolbarItemAction()
            setOrderRejectButton()
        }
    }

    private fun showSampleData(isLoading: Boolean) {
        if (isLoading) {
            binding.layoutProductDataShimmer.startShimmer()
            binding.layoutProductDataShimmer.visibility = View.VISIBLE
            binding.layoutProductData.visibility = View.GONE
        } else {
            binding.layoutProductDataShimmer.stopShimmer()
            binding.layoutProductDataShimmer.visibility = View.GONE
            binding.layoutProductData.visibility = View.VISIBLE
        }
    }

    private fun setToolbarItemAction() {
        binding.toolbarOrderManagementDetail.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setOrderRejectButton() {
        binding.buttonOrderReject.setOnClickListener {
            // 다이얼로그 커스텀 필요
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("주문 거부")
                .setMessage("주문을 거부 하시겠습니까?")
                .setPositiveButton("확인", null)
                .setNegativeButton("취소", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}