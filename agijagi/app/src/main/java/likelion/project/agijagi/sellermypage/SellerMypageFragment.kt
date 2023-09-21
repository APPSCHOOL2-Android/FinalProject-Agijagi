package likelion.project.agijagi.sellermypage

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentSellerMypageBinding
import likelion.project.agijagi.model.SellerModel
import likelion.project.agijagi.model.UserModel

class SellerMypageFragment : Fragment() {

    private var _binding: FragmentSellerMypageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerMypageBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateTextViews()
        setToolbarMenuItem()
        setSellerMyPageMenu()
    }

    private fun updateTextViews() {
        binding.run {
            val name = "${SellerModel.businessName}님 안녕하세요!"
            textviewSellerMyPageStoreName.text = name
            val email = UserModel.email
            textviewSellerMyPageStoreEmail.text = email
        }
    }

    private fun logout() {
        UserModel.clearData()
        Firebase.auth.signOut()
        findNavController().navigate(R.id.action_sellerMypageFragment_to_loginFragment)
    }

    private fun delete() {
        UserModel.clearData()
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
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("로그아웃")
                    .setMessage("로그아웃 합니다.")
                    .setPositiveButton("확인") { _: DialogInterface, _: Int ->
                        logout()
                    }
                    .setNegativeButton("취소", null)
                    .show()
            }

            textviewSellerMyPageQuit.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("회원탈퇴")
                    .setMessage("${SellerModel.businessName}님 정말 떠나실 건가요?\n너무 아쉬워요.")
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

}