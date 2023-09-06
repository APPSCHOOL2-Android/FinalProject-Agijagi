package likelion.project.agijagi.buyermypage

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentBuyerMypageBinding

class BuyerMypageFragment : Fragment() {

    private var _binding: FragmentBuyerMypageBinding? = null
    private val binding get() = _binding!!
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBuyerMypageBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarMenuItem()
        setBuyerMyPageMenu()
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
                // 다이얼로그 커스텀 필요
                MaterialAlertDialogBuilder(mainActivity)
                    .setTitle("로그아웃")
                    .setMessage("로그아웃 하시겠습니까?")
                    .setPositiveButton("확인") { _: DialogInterface, _: Int ->

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