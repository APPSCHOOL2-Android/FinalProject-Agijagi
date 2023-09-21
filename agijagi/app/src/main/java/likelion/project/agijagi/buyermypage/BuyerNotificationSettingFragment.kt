package likelion.project.agijagi.buyermypage

import android.os.Bundle
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
import likelion.project.agijagi.databinding.FragmentBuyerNotificationSettingBinding
import likelion.project.agijagi.model.UserModel

class BuyerNotificationSettingFragment : Fragment() {

    private var _binding: FragmentBuyerNotificationSettingBinding? = null
    private val binding get()= _binding!!

    private lateinit var db:FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBuyerNotificationSettingBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarItemAction()
        setup()
        init()

        binding.run {
            switchBuyerNotificationSettingOrder.setOnCheckedChangeListener { compoundButton, check ->
                updateSettings()
            }
            switchBuyerNotificationSettingMade.setOnCheckedChangeListener { compoundButton, check ->
                updateSettings()
            }
            switchBuyerNotificationSettingQa.setOnCheckedChangeListener { compoundButton, check ->
                updateSettings()
            }
        }
    }

    private fun setToolbarItemAction() {
        binding.toolbarBuyerNotificationSetting.setNavigationOnClickListener {
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
        db.collection("buyer").document(UserModel.roleId).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // 업데이트 성공 시 동작
                    Snackbar.make(binding.root, "성공", Snackbar.LENGTH_SHORT).show()
                    val data = it.result.data

                    val map = data?.get("notif_setting") as MutableMap<String, Boolean>
                    // 초기 설정
                    binding.run {
                        switchBuyerNotificationSettingOrder.isChecked = map["delivery_alert"]!!
                        switchBuyerNotificationSettingMade.isChecked = map["making_alert"]!!
                        switchBuyerNotificationSettingQa.isChecked = map["cs_alert"]!!
                    }
                } else {
                    // 통신 실패 시 동작
                    Snackbar.make(binding.root, "실패", Snackbar.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateSettings() {
        // update data
        val (order, made, qa) = arrayOf(
            binding.switchBuyerNotificationSettingOrder.isChecked,
            binding.switchBuyerNotificationSettingMade.isChecked,
            binding.switchBuyerNotificationSettingQa.isChecked
        )

        val user = db.collection("buyer").document(UserModel.roleId)
        val value =
            mutableMapOf<String, Boolean>(
                "delivery_alert" to order,
                "making_alert" to made,
                "cs_alert" to qa
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