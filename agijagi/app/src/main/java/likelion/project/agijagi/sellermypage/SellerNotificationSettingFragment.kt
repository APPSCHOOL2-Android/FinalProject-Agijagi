package likelion.project.agijagi.sellermypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import likelion.project.agijagi.Essential
import likelion.project.agijagi.databinding.FragmentSellerNotificationSettingBinding

class SellerNotificationSettingFragment : Fragment() {

    private var _binding: FragmentSellerNotificationSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerNotificationSettingBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarItemAction()
        init()

        binding.run {
            switchSellerNotificationSettingInquiry.setOnCheckedChangeListener { compoundButton, check ->
                updateSettings()
            }
            switchSellerNotificationSettingOrder.setOnCheckedChangeListener { compoundButton, check ->
                updateSettings()
            }
            switchSellerNotificationSettingExchange.setOnCheckedChangeListener { compoundButton, check ->
                updateSettings()
            }
        }
    }

    private fun setToolbarItemAction() {
        binding.toolbarSellerNotificationSetting.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun init() {
        // download from server
        Essential.db.collection("seller").document(Essential.roleId).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // 업데이트 성공 시 동작
                    Snackbar.make(binding.root, "성공", Snackbar.LENGTH_SHORT).show()
                    val data = it.result.data

                    val map = data?.get("notif_setting") as MutableMap<String, Boolean>
                    // 초기 설정
                    binding.run {
                        switchSellerNotificationSettingInquiry.isChecked = map["inquiry"]!!
                        switchSellerNotificationSettingOrder.isChecked = map["order"]!!
                        switchSellerNotificationSettingExchange.isChecked = map["exchange"]!!
                    }
                } else {
                    // 통신 실패 시 동작
                    Snackbar.make(binding.root, "실패", Snackbar.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateSettings() {
        // update data
        val (inquiry, order, exchange) = arrayOf(
            binding.switchSellerNotificationSettingInquiry.isChecked,
            binding.switchSellerNotificationSettingOrder.isChecked,
            binding.switchSellerNotificationSettingExchange.isChecked
        )

        val user = Essential.db.collection("seller").document(Essential.roleId)
        val value =
            mutableMapOf<String, Boolean>(
                "exchange" to exchange,
                "inquiry" to inquiry,
                "order" to order
            )
        user.update("notif_setting", value).addOnCompleteListener {
            if (it.isSuccessful) {
                // 업데이트 성공 시 동작
                Snackbar.make(binding.root, "성공", Snackbar.LENGTH_SHORT).show()
            } else {
                // 통신 실패 시 동작
                Snackbar.make(binding.root, "실패", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}