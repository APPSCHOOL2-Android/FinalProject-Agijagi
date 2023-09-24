package likelion.project.agijagi.buyermypage

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import likelion.project.agijagi.R
import likelion.project.agijagi.buyermypage.adapter.ShippingManagementAdapter
import likelion.project.agijagi.databinding.FragmentShippingManagementBinding
import likelion.project.agijagi.model.ShippingAddress
import likelion.project.agijagi.model.UserModel

class ShippingManagementFragment : Fragment() {

    private var _binding: FragmentShippingManagementBinding? = null
    private val binding get() = _binding!!
    lateinit var shippingManagementAdapter: ShippingManagementAdapter

    val db = FirebaseFirestore.getInstance()
    var showCheckBox: Boolean = false

    companion object {
        val shippingManagementList = mutableListOf<ShippingAddress>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShippingManagementBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getShowCheckBoxState()
        Log.d("ShippingManagementFragment.onViewCreated()", showCheckBox.toString())

        setToolbarItemAction()
        setShippingAddButton()
        getShippingData()

        shippingManagementAdapter = ShippingManagementAdapter(showCheckBox)

        binding.run {
            recyclerviewShippingManagement.run {
                adapter = shippingManagementAdapter
                layoutManager = LinearLayoutManager(context)

                addItemDecoration(MarginItemDecoration(40))
            }
        }
    }

    // 배송지 데이터 불러오기
    private fun getShippingData() {
        // 리스트 초기화
        shippingManagementList.clear()

        db.collection("buyer").document(UserModel.roleId).collection("shipping_address")
            .get().addOnSuccessListener {
                Log.d("shippingList", "데이터 불러오기 성공")
                for (shippingData in it) {
                    val uid = shippingData.id
                    val address = shippingData.getString("address") ?: ""
                    val addressDetail = shippingData.getString("address_detail") ?: ""
                    val shipping_name = shippingData.getString("shipping_name") ?: ""
                    val phone_number = shippingData.getString("phone_number") ?: ""
                    val recipient = shippingData.getString("recipient") ?: ""
                    Log.d("shippingList", addressDetail)
                    shippingManagementList.add(
                        ShippingAddress(
                            false,
                            uid,
                            address,
                            addressDetail,
                            phone_number,
                            recipient,
                            shipping_name
                        )
                    )
                }
                shippingManagementAdapter.submitList(shippingManagementList)
                Log.d(
                    "ShippingManagementFragment.getShippingData()",
                    shippingManagementList.toString()
                )

                // dataList가 비어있을 때 보이도록
                if (shippingManagementList.isEmpty()) {
                    binding.textViewShippingManagemnetNull.visibility = View.VISIBLE
                } else {
                    binding.textViewShippingManagemnetNull.visibility = View.GONE
                }
            }
    }

    private fun getShowCheckBoxState() {
        arguments?.getBoolean("payment_to_shippingManagement")?.let {
            showCheckBox = it
            if (showCheckBox) binding.toolbarShippingManagement.title = "배송지 변경"
        }
    }

    private fun setToolbarItemAction() {
        binding.toolbarShippingManagement.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setShippingAddButton() {
        binding.buttonShippingManagementAdd.setOnClickListener {
            findNavController().navigate(R.id.action_shippingManagementFragment_to_shippingAddFragment)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}