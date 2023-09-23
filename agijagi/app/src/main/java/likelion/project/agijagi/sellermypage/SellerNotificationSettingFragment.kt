package likelion.project.agijagi.sellermypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.databinding.FragmentSellerNotificationSettingBinding
import likelion.project.agijagi.model.UserModel

class SellerNotificationSettingFragment : Fragment() {

    private var _binding: FragmentSellerNotificationSettingBinding? = null
    private val binding get() = _binding!!

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

        setToolbarItemAction()
        setup()
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

    fun setup() {
        db = Firebase.firestore

        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
    }

    private fun init() {
        // download from server
        db.collection("seller").document(UserModel.roleId).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // 업데이트 성공 시 동작
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
                    Log.e("FirebaseException", it.exception.toString())
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

        val user = db.collection("seller").document(UserModel.roleId)
        val value =
            mutableMapOf<String, Boolean>(
                "exchange" to exchange,
                "inquiry" to inquiry,
                "order" to order
            )
        user.update("notif_setting", value).addOnCompleteListener {
            if (it.isSuccessful) {
                // 업데이트 성공 시 동작
            } else {
                // 통신 실패 시 동작
                Log.e("FirebaseException", it.exception.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}