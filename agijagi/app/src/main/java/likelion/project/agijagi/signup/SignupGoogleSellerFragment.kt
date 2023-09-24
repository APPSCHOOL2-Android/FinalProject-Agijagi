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
import likelion.project.agijagi.databinding.FragmentGoogleSignupSellerBinding

class SignupGoogleSellerFragment : Fragment() {

    private var _binding: FragmentGoogleSignupSellerBinding? = null
    private val binding get() = _binding!!

    private var auth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore

    private val mAuth = Firebase.auth.currentUser

    private var businessNameState = false
    private var representativeNameState = false
    private var registrationNumberState = false
    private var businessAddressState = false
    private var businessNumberState = false

    private var buttonState = false

    private var roleId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGoogleSignupSellerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        setup()

        binding.run {
            toolbarGoogleSignupSellerToolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            editinputGoogleSignupSellerBusinessName.doAfterTextChanged { businessName ->
                businessNameState = businessName.toString().isNotBlank()
                setSignupButtonState(isValidSignupButton())
            }

            editinputGoogleSignupSellerRepresentativeName.doAfterTextChanged { representativeName ->
                representativeNameState = representativeName.toString().isNotBlank()
                setSignupButtonState(isValidSignupButton())
            }

            editinputGoogleSignupSellerRegistrationNumber.doAfterTextChanged { registrationNumber ->
                registrationNumberState = registrationNumber.toString().isNotBlank()
                setSignupButtonState(isValidSignupButton())
            }

            editinputGoogleSignupSellerBusinessAddress.doAfterTextChanged { businessAddress ->
                businessAddressState = businessAddress.toString().isNotBlank()
                setSignupButtonState(isValidSignupButton())
            }

            editinputGoogleSignupSellerBusinessNumber.doAfterTextChanged { businessNumber ->
                businessNumberState = businessNumber.toString().isNotBlank()
                setSignupButtonState(isValidSignupButton())
            }

            buttonGoogleSignupSellerComplete.setOnClickListener {
                if (buttonState == true) {
                    createUser()
                } else {
                    showSnackBar("회원가입 실패했습니다.")
                }
            }
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setAnchorView(binding.buttonGoogleSignupSellerComplete)
            .show()
    }

    private fun isValidSignupButton(): Boolean {
        buttonState = businessNameState && representativeNameState && registrationNumberState &&
                businessAddressState && businessNumberState
        return buttonState
    }

    private fun setSignupButtonState(state: Boolean) {
        if (state) {
            binding.buttonGoogleSignupSellerComplete.isSelected = true
            binding.buttonGoogleSignupSellerComplete.setTextColor(resources.getColor(R.color.white))
        } else {
            binding.buttonGoogleSignupSellerComplete.isSelected = false
            binding.buttonGoogleSignupSellerComplete.setTextColor(resources.getColor(R.color.jagi_hint_color))
        }
    }

    private fun createUser() {
        val sellerSetting = hashMapOf(
            "exchange" to false,
            "inquiry" to false,
            "order" to false
        )

        val sellerInfo = hashMapOf(
            "address" to binding.editinputGoogleSignupSellerBusinessAddress.text.toString(),
            "br_cert" to "",
            "brn" to binding.editinputGoogleSignupSellerRegistrationNumber.text.toString(),
            "bussiness_name" to binding.editinputGoogleSignupSellerBusinessName.text.toString(),
            "notif_setting" to sellerSetting,
            "tel" to binding.editinputGoogleSignupSellerBusinessNumber.text.toString()
        )

        db.collection("seller").add(sellerInfo)
            .addOnSuccessListener {
                roleId = it.id

                val userInfo = hashMapOf(
                    "email" to mAuth?.email.toString(),
                    "password" to "",
                    "name" to binding.editinputGoogleSignupSellerRepresentativeName.text.toString(),
                    "google_login_check" to true,
                    "email_notif" to false,
                    "sms_notif" to false,
                    "is_seller" to true,
                    "role_id" to roleId,
                    "new_chat_count" to 0,
                    "new_notif_count" to 0
                )

                db.collection("user").document(mAuth?.uid.toString())
                    .set(userInfo, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d(
                            "SignupGoogleSellerFragment.createUser()",
                            "user cloud firestore 등록 완료\n" +
                                    " authUID: ${mAuth?.uid}"
                        )
                        findNavController().navigate(R.id.action_signupGoogleSellerFragment_to_loginFragment)
                    }
                    .addOnFailureListener { e ->
                        Log.e(
                            "FirebaseException",
                            "user cloud firestore 등록 실패: ${e.stackTrace}",
                            e
                        )
                    }
            }
            .addOnFailureListener {
                showSnackBar("회원가입 실패")
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