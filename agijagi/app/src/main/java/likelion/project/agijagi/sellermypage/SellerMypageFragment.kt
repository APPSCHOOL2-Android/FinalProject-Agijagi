package likelion.project.agijagi.sellermypage

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentSellerMypageBinding

class SellerMypageFragment : Fragment() {

    private var auth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore

    private var _binding: FragmentSellerMypageBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity

    private var _name: String = ""
    private var email: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = FirebaseAuth.getInstance()
        _binding = FragmentSellerMypageBinding.inflate(inflater,container,false)
        mainActivity = activity as MainActivity

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        setToolbarMenuItem()
        setSellerMyPageMenu()
        setName_Email()
    }

    private fun setName_Email() {
        db.collection("user").document(auth?.currentUser?.email.toString())
            .get()
            .addOnSuccessListener {
                _name = it.getString("name") ?: ""
                email = it.getString("email") ?: ""
                Log.d("textview", "현재 name: $_name")
                Log.d("textview", "현재 email: $email")

                // 데이터를 가져온 후 TextView 업데이트
                updateTextViews()
            }
    }

    private fun updateTextViews() {
        _binding?.run {
            val name = "${_name}님 안녕하세요!"
            textviewSellerMyPageStoreName.text = name
            textviewSellerMyPageStoreEmail.text = email
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        findNavController().navigate(R.id.action_sellerMypageFragment_to_loginFragment)
    }

    private fun delete(){
        FirebaseAuth.getInstance().currentUser?.delete()
        findNavController().navigate(R.id.action_sellerMypageFragment_to_loginFragment)
    }

    private fun setToolbarMenuItem() {
        binding.toolbarSellerMyPage.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_seller_my_page_chat -> {
                    findNavController().navigate(R.id.action_sellerMypageFragment_to_chattingListFragment)
                }

                R.id.menu_seller_my_page_notification -> {
                    findNavController().navigate(R.id.action_sellerMypageFragment_to_notificationListFragment)
                }
            }
            false
        }
    }

    private fun setSellerMyPageMenu() {
        binding.run {
            textviewSellerMyPageManagementMyStore.setOnClickListener {
                findNavController().navigate(R.id.action_sellerMypageFragment_to_storeManagementFragment)
            }

            textviewSellerMyPageProductAdd.setOnClickListener {
                findNavController().navigate(R.id.action_sellerMypageFragment_to_productAddFragment)
            }

            textviewSellerMyPageProductList.setOnClickListener {
                findNavController().navigate(R.id.action_sellerMypageFragment_to_productListFragment)
            }

            textviewSellerMyPageOrderCancelChange.visibility = View.GONE
            line4.visibility = View.GONE

            textviewSellerMyPageManagementOrderShip.setOnClickListener {
                findNavController().navigate(R.id.action_sellerMypageFragment_to_orderManagementFragment)
            }

            textviewSellerMyPageSettingNotification.setOnClickListener {
                findNavController().navigate(R.id.action_sellerMypageFragment_to_sellerNotificationSettingFragment)
            }

            textviewSellerMyPageLogout.setOnClickListener {
                // 다이얼로그 커스텀 필요
                MaterialAlertDialogBuilder(mainActivity)
                    .setTitle("로그아웃")
                    .setMessage("로그아웃 하시겠습니까?")
                    .setPositiveButton("확인") { _: DialogInterface, _: Int ->
                        logout()
                    }
                    .setNegativeButton("취소", null)
                    .show()
            }

            textviewSellerMyPageQuit.setOnClickListener {
                // 다이얼로그 커스텀 필요
                MaterialAlertDialogBuilder(mainActivity)
                    .setTitle("회원탈퇴")
                    .setMessage("고길동님 정말 떠나실 건가요?\n너무 아쉬워요.")
                    .setPositiveButton("확인") { _: DialogInterface, _: Int ->
                        delete()
                    }
                    .setNegativeButton("취소", null)
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setup() {
        db = Firebase.firestore

        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
    }
}