package likelion.project.agijagi.buyermypage

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentBuyerMypageBinding
import likelion.project.agijagi.databinding.FragmentNoLoginMypageBinding

class BuyerMypageFragment : Fragment() {

    private var auth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore

    private var _binding: FragmentBuyerMypageBinding? = null
//    private val binding get() = _binding!!

    private var _noLoginMyPageFragmentBinding : FragmentNoLoginMypageBinding? = null
//    private val noLoginMyPageFragmentBinding get() = _noLoginMyPageFragmentBinding!!

    lateinit var mainActivity: MainActivity

    private var _name: String = ""
    private var email: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = FirebaseAuth.getInstance()
        Log.d("auth","auth : ${auth?.currentUser}")
        if (auth?.currentUser != null) {
            _binding = FragmentBuyerMypageBinding.inflate(inflater,container,false)
            mainActivity = activity as MainActivity
            return _binding?.root
        } else {
            _noLoginMyPageFragmentBinding = FragmentNoLoginMypageBinding.inflate(inflater,container,false)
            return _noLoginMyPageFragmentBinding?.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        if(view == _noLoginMyPageFragmentBinding?.root) {
            _noLoginMyPageFragmentBinding?.textviewMyPageLogin?.setOnClickListener {
                login()
            }
        } else {
            setToolbarMenuItem()
            setBuyerMyPageMenu()
            setName_Email()
        }
    }

    private fun setName_Email() {
        Log.d("email","현재 email: ${auth?.currentUser?.email.toString()}")
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
            textviewBuyerMyPageName.text = name
            textviewBuyerMyPageEmail.text = email
        }
    }

    private fun login() {
        findNavController().navigate(R.id.action_buyerMypageFragment_to_loginFragment)
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        findNavController().navigate(R.id.action_buyerMypageFragment_to_loginFragment)
    }

    private fun delete(){
        FirebaseAuth.getInstance().currentUser?.delete()
        findNavController().navigate(R.id.action_buyerMypageFragment_to_loginFragment)
    }

    private fun setToolbarMenuItem() {
        _binding?.toolbarBuyerMyPage?.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_buyer_my_page_chat -> {
                    findNavController().navigate(R.id.action_buyerMypageFragment_to_chattingListFragment)
                }
                R.id.menu_buyer_my_page_notify -> {
                    findNavController().navigate(R.id.action_buyerMypageFragment_to_notificationListFragment)
                }
                R.id.menu_buyer_my_page_shopping_bag -> {
                    findNavController().navigate(R.id.action_buyerMypageFragment_to_shoppingListFragment)
                }
            }
            false
        }
    }
    private fun setBuyerMyPageMenu() {
        _binding?.run {
            textviewBuyerMyPageProfileManagement.setOnClickListener {
                findNavController().navigate(R.id.action_buyerMypageFragment_to_profileManagementFragment)
            }

            textviewBuyerMyPageShippingManagement.setOnClickListener {
                findNavController().navigate(R.id.action_buyerMypageFragment_to_shippingManagementFragment)
            }

            textviewBuyerMyPageNotificationSetting.setOnClickListener {
                findNavController().navigate(R.id.action_buyerMypageFragment_to_alarmManagementFragment)
            }

            textviewBuyerMyPageLogout.setOnClickListener {
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

            textviewBuyerMyPageQuit.setOnClickListener {
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
        _noLoginMyPageFragmentBinding = null
    }

    private fun setup() {
        db = Firebase.firestore

        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
    }

}