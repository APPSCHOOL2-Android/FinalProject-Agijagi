package likelion.project.agijagi.buyermypage

import android.graphics.Rect
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import likelion.project.agijagi.R
import likelion.project.agijagi.UserEssential.Companion.auth
import likelion.project.agijagi.UserEssential.Companion.db
import likelion.project.agijagi.UserEssential.Companion.roleId
import likelion.project.agijagi.buyermypage.adapter.ShippingManagementAdapter
import likelion.project.agijagi.buyermypage.model.ShippingManagementModel
import likelion.project.agijagi.databinding.FragmentShippingManagementBinding
import kotlin.concurrent.thread

class ShippingManagementFragment : Fragment() {

    private var _binding: FragmentShippingManagementBinding? = null
    private val binding get() = _binding!!
    lateinit var shippingManagementAdapter: ShippingManagementAdapter

    private var auth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore

    companion object {
        val shippingManagementList = mutableListOf<ShippingManagementModel>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShippingManagementBinding.inflate(inflater)
        auth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setup()
        setToolbarItemAction()
        setShippingAddButton()
        getShippingData()

        shippingManagementAdapter = ShippingManagementAdapter()

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

        val userUid = auth?.currentUser?.uid.toString()

        // 리스트 초기화
        shippingManagementList.clear()

        // userEssential 사용 할 때 코드
//        db.collection("buyer").document(roleId).collection("shipping_address")
//            .get().addOnSuccessListener {
//                Log.d("shippingList", "데이터 불러오기 성공")
//                for (shippingData in it) {
//                    val address = shippingData.getString("address") ?: ""
//                    val addressDetail = shippingData.getString("address_detail") ?: ""
//                    val basic = shippingData.getBoolean("basic") ?: false
//                    val title = shippingData.getString("shipping_name") ?: ""
//                    val recipientPhone = shippingData.getString("phone_number") ?: ""
//                    val recipient = shippingData.getString("recipient") ?: ""
//                    Log.d("shippingList", addressDetail)
//                    shippingManagementList.add(
//                        ShippingManagementModel(
//                            address,
//                            addressDetail,
//                            basic,
//                            title,
//                            recipientPhone,
//                            recipient
//                        )
//                    )
//                }
//                shippingManagementAdapter.submitList(shippingManagementList)
//                Log.d("shippingList", shippingManagementList.toString())
//
//                // dataList가 비어있을 때 보이도록
//                if (shippingManagementList.isEmpty()) {
//                    binding.textViewShippingManagemnetNull.visibility = View.VISIBLE
//                } else {
//                    binding.textViewShippingManagemnetNull.visibility = View.GONE
//                }
//            }

        // 테스트용 코드 (db, auth, setup 이랑 같이 지울코드)
        db.collection("buyer").document(userUid).collection("shipping_address")
            .get().addOnSuccessListener {
                Log.d("shippingList", "데이터 불러오기 성공")
                for (shippingData in it) {
                    val uid = shippingData.id
                    val address = shippingData.getString("address") ?: ""
                    val addressDetail = shippingData.getString("address_detail") ?: ""
                    val basic = shippingData.getBoolean("basic") ?: false
                    val title = shippingData.getString("shipping_name") ?: ""
                    val recipientPhone = shippingData.getString("phone_number") ?: ""
                    val recipient = shippingData.getString("recipient") ?: ""
                    Log.d("shippingList", addressDetail)
                    shippingManagementList.add(
                        ShippingManagementModel(
                            uid,
                            address,
                            addressDetail,
                            basic,
                            title,
                            recipientPhone,
                            recipient
                        )
                    )
                }
                shippingManagementAdapter.submitList(shippingManagementList)
                Log.d("shippingList", shippingManagementList.toString())

                // dataList가 비어있을 때 보이도록
                if (shippingManagementList.isEmpty()) {
                    binding.textViewShippingManagemnetNull.visibility = View.VISIBLE
                } else {
                    binding.textViewShippingManagemnetNull.visibility = View.GONE
                }
            }
    }

    private fun setup() {
        db = Firebase.firestore

        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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