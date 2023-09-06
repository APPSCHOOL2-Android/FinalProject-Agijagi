package likelion.project.agijagi.sellermypage

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentSellerMypageBinding

class SellerMypageFragment : Fragment() {

    private var _binding: FragmentSellerMypageBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerMypageBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarMenuItem()
        setSellerMyPageMenu()
    }

    private fun setToolbarMenuItem() {
        binding.toolbarSellerMyPage.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_seller_my_page_chat -> {
                    findNavController().navigate(R.id.action_sellerMypageFragment_to_userChatListFragment)
                }

                R.id.menu_seller_my_page_notification -> {
                    findNavController().navigate(R.id.action_sellerMypageFragment_to_notificationFragment)
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