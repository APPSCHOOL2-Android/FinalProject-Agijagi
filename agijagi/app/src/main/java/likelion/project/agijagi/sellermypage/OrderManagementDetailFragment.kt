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
import likelion.project.agijagi.databinding.FragmentOrderManagementDetailBinding
import likelion.project.agijagi.sellermypage.model.OrderManagementModel


class OrderManagementDetailFragment : Fragment() {
    private var _binding: FragmentOrderManagementDetailBinding? = null
    private val binding get() = _binding!!
    lateinit var mainActivity: MainActivity

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

        if (arguments?.getParcelable<OrderManagementModel>("orderManagementDetail") != null) {
            val item = arguments?.getParcelable<OrderManagementModel>("orderManagementDetail")
            val name = item?.name
            val price = item?.price

            binding.run {
                textviewOrderDetailName.text = name
                textviewOrderDetailPrice.text = price
            }

            setToolbarItemAction()
            setOrderRejectButton()
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
            MaterialAlertDialogBuilder(mainActivity)
                .setTitle("주문 거부")
                .setMessage("주문을 거부 하시겠습니까?")
                .setPositiveButton("확인") { _: DialogInterface, _: Int ->

                }
                .setNegativeButton("취소", null)
                .show()
        }
    }

}