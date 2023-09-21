package likelion.project.agijagi.buyermypage

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentBuyerMypageBinding
import likelion.project.agijagi.model.UserModel

class BuyerMypageFragment : Fragment() {

    private var _binding: FragmentBuyerMypageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBuyerMypageBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateTextViews()
        setToolbarMenuItem()
        setBuyerMyPageMenu()
    }

    private fun updateTextViews() {
        binding.run {
            val name = "${BuyerModel.nickname}님 안녕하세요!"
            textviewBuyerMyPageName.text = name
            val email = UserModel.email
            textviewBuyerMyPageEmail.text = email
        }
    }

    private fun logout() {
        UserModel.clearData()
        FirebaseAuth.getInstance().signOut()
        findNavController().navigate(R.id.action_buyerMypageFragment_to_loginFragment)
    }

    // 회원탈퇴
    private fun delete() {
        UserModel.clearData()
        FirebaseAuth.getInstance().currentUser?.delete()
        findNavController().navigate(R.id.action_buyerMypageFragment_to_loginFragment)
    }

    private fun setToolbarMenuItem() {
        binding.toolbarBuyerMyPage.setOnMenuItemClickListener {
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
        binding.run {
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
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("로그아웃")
                    .setMessage("로그아웃 합니다.")
                    .setPositiveButton("확인") { _: DialogInterface, _: Int ->
                        logout()
                    }
                    .setNegativeButton("취소", null)
                    .show()
            }

            textviewBuyerMyPageQuit.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("회원탈퇴")
                    .setMessage("${UserModel.name}님 정말 떠나실 건가요?\n너무 아쉬워요.")
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
