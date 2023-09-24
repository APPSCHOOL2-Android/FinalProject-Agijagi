package likelion.project.agijagi.signup

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentGoogleSignupBuyerBinding

class SignupGoogleBuyerFragment : Fragment() {

    private var _binding: FragmentGoogleSignupBuyerBinding? = null
    private val binding get() = _binding!!

    private var auth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore

    private val mAuth = Firebase.auth.currentUser

    private var nickNameState = false

    private var roleId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentGoogleSignupBuyerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        setup()

        binding.run {
            toolbarGoogleSignupBuyerToolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            editinputGoogleSignupBuyerNickname.doAfterTextChanged { nickname ->
                nickNameState = nickname.toString().length in 2..4
                setSignupButtonState(nickNameState)
            }

            buttonGoogleSignupBuyerComplete.setOnClickListener {
                if (nickNameState == true) {
                    createUser()
                } else {
                    showSnackBar("회원가입 실패했습니다.")
                }
            }
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setAnchorView(binding.buttonGoogleSignupBuyerComplete)
            .show()
    }

    private fun setSignupButtonState(state: Boolean) {
        if (state) {
            binding.buttonGoogleSignupBuyerComplete.isSelected = true
            binding.buttonGoogleSignupBuyerComplete.setTextColor(
                resources.getColor(
                    R.color.white
                )
            )
        } else {
            binding.buttonGoogleSignupBuyerComplete.isSelected = false
            binding.buttonGoogleSignupBuyerComplete.setTextColor(
                resources.getColor(
                    R.color.jagi_hint_color
                )
            )
        }
    }

    private fun createUser() {
        val notifSetting = hashMapOf(
            "cs_alert" to false,
            "delivery_alert" to false,
            "making_alert" to false
        )

        val buyerInfo = hashMapOf(
            "nickname" to binding.editinputGoogleSignupBuyerNickname.text.toString(),
            "notif_setting" to notifSetting,
            "basic" to ""
        )

        db.collection("buyer").add(buyerInfo).addOnSuccessListener {
            roleId = it.id

            val shipping_info = hashMapOf(
                "address" to "",
                "address_detail" to "",
                "phone_number" to "",
                "recipient" to "",
                "shipping_name" to "",
            )

            db.collection("shipping_address").add(shipping_info)

            val userInfo = hashMapOf(
                "email" to mAuth?.email.toString(),
                "password" to "",
                "name" to mAuth?.displayName.toString(),
                "google_login_check" to true,
                "email_notif" to false,
                "sms_notif" to false,
                "is_seller" to false,
                "role_id" to roleId,
                "new_chat_count" to 0,
                "new_notif_count" to 0
            )

            db.collection("user").document(mAuth?.uid.toString())
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d(
                        "SignupGoogleBuyerFragment.createUser()",
                        "user cloud firestore 등록 완료\n authUID: ${mAuth?.uid}"
                    )
                    findNavController().navigate(R.id.action_signupGoogleBuyerFragment_to_loginFragment)
                    showSnackBar("회원가입  성공하였습니다.")
                }
                .addOnFailureListener { e ->
                    Log.e(
                        "FirebaseException",
                        "user cloud firestore 등록 실패: ${e.stackTrace}",
                        e
                    )
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}