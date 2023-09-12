package likelion.project.agijagi.sellermypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.databinding.FragmentSellerNotificationSettingBinding

class SellerNotificationSettingFragment : Fragment() {

    private var _binding: FragmentSellerNotificationSettingBinding? = null
    private val binding get() = _binding!!

    private var auth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerNotificationSettingBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        setup()

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
        val seller_pk = "OMumvFq5CqKVw5n7IGjX" // 테스트용

        db.collection("seller").document(seller_pk).get().addOnCompleteListener {
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

    private fun setup() {
        db = Firebase.firestore

        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
    }

    private fun updateSettings() {
        // 데이터를 서버에 저장
        val (inquiry, order, exchange) = arrayOf(
            binding.switchSellerNotificationSettingInquiry.isChecked,
            binding.switchSellerNotificationSettingOrder.isChecked,
            binding.switchSellerNotificationSettingExchange.isChecked
        )
        val seller_pk = "OMumvFq5CqKVw5n7IGjX" // 테스트용

        val user = db.collection("seller").document(seller_pk)
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