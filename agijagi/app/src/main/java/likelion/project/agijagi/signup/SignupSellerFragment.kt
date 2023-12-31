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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentSignupSellerBinding
import java.util.regex.Pattern

class SignupSellerFragment : Fragment() {

    private var _binding: FragmentSignupSellerBinding? = null
    private val binding get() = _binding!!

    private var auth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore

    private val emailValidation =
        "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

    private var businessNameState = false
    private var registrationNumberState = false
    private var representativeNameState = false
    private var emailState = false
    private var passWordState = false
    private var businessAddressState = false
    private var businessNumberState = false

    private var buttonState = false

    private var roleId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupSellerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        setup()

        binding.run {
            toolbarSignupSellerToolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            // 상호명
            editinputSignupSellerBusinessName.doAfterTextChanged { businessName ->
                businessNameState = businessName.toString().isNotBlank()
                setSignupButtonState(isValidSignupButton())
            }

            // 사업자 등록번호
            editinputSignupSellerRegistrationNumber.doAfterTextChanged { registrationNumber ->
                registrationNumberState = registrationNumber.toString().isNotBlank()
                setSignupButtonState(isValidSignupButton())
            }

            // 대표자명
            editinputSignupSellerRepresentativeName.doAfterTextChanged { representativeName ->
                representativeNameState = representativeName.toString().length in 2..4
                setSignupButtonState(isValidSignupButton())
            }

            // email
            editinputSignupSellerEmail.doAfterTextChanged { email ->
                emailState = Pattern.matches(emailValidation, email)
                setSignupButtonState(isValidSignupButton())
            }

            // password
            editinputSignupSellerPassword.doAfterTextChanged { password ->
                isValiedPassWord()
                setSignupButtonState(isValidSignupButton())
            }

            // passwordCheck
            editinputSignupSellerCheckPassword.doAfterTextChanged { passwordCheck ->
                isValiedPassWord()
                setSignupButtonState(isValidSignupButton())
            }

            // 사업장 주소
            editinputSignupSellerBusinessAddress.doAfterTextChanged { businessAddress ->
                businessAddressState = businessAddress.toString().isNotBlank()
                setSignupButtonState(isValidSignupButton())
            }

            // 사업장 전화번호
            editinputSignupSellerBusinessNumber.doAfterTextChanged { businessNumber ->
                businessNumberState = businessNumber.toString().isNotBlank()
                setSignupButtonState(isValidSignupButton())
            }

            // 사업자 등록증 추가 버튼
            buttonSignupSellerAddFile.setOnClickListener {

            }

            buttonSignupSellerComplete.setOnClickListener {
                if (buttonState == true) {
                    createUser(
                        email = editinputSignupSellerEmail.text.toString(),
                        password = editinputSignupSellerPassword.text.toString()
                    )
                } else {
                    showSnackBar("회원가입 실패했습니다.")
                }
            }
        }
    }

    private fun isValiedPassWord() {
        binding.run {
            passWordState =
                editinputSignupSellerPassword.text.toString() == editinputSignupSellerCheckPassword.text.toString() &&
                        editinputSignupSellerPassword.text.toString().isNotBlank() &&
                        editinputSignupSellerCheckPassword.text.toString().isNotBlank() &&
                        editinputSignupSellerPassword.text.toString().length in (6..15) &&
                        editinputSignupSellerCheckPassword.text.toString().length in (6..15)
        }
    }

    private fun isValidSignupButton(): Boolean {
        buttonState = businessNameState && registrationNumberState && representativeNameState &&
                emailState && passWordState && businessAddressState && businessNumberState
        return buttonState
    }

    private fun setSignupButtonState(state: Boolean) {
        if (state) {
            binding.buttonSignupSellerComplete.isSelected = true
            binding.buttonSignupSellerComplete.setTextColor(resources.getColor(R.color.white))
        } else {
            binding.buttonSignupSellerComplete.isSelected = false
            binding.buttonSignupSellerComplete.setTextColor(resources.getColor(R.color.jagi_hint_color))

        }
    }

    private fun createUser(email: String, password: String) {
        if (_binding != null) {

            auth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener { task ->
                    // 이메일 형식 체크
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        if (task.isSuccessful) {
                            val user = auth?.currentUser

                            val sellerSetting = hashMapOf(
                                "exchange" to false,
                                "inquiry" to false,
                                "order" to false
                            )

                            val sellerInfo = hashMapOf(
                                "address" to binding.editinputSignupSellerBusinessAddress.text.toString(),
                                "br_cert" to "",
                                "brn" to binding.editinputSignupSellerRegistrationNumber.text.toString(),
                                "bussiness_name" to binding.editinputSignupSellerBusinessName.text.toString(),
                                "notif_setting" to sellerSetting,
                                "tel" to binding.editinputSignupSellerBusinessNumber.text.toString()
                            )

                            db.collection("seller")
                                .add(sellerInfo)
                                .addOnSuccessListener { documentReference ->
                                    roleId = documentReference.id
                                    Log.d("SignupSellerFragment.createUser()", "roleID: ${roleId}")

                                    val userInfo = hashMapOf(
                                        "email" to email,
                                        "password" to password,
                                        "name" to binding.editinputSignupSellerRepresentativeName.text.toString(),
                                        "google_login_check" to false,
                                        "email_notif" to false,
                                        "sms_notif" to false,
                                        "is_seller" to true,
                                        "role_id" to roleId,
                                        "new_chat_count" to 0,
                                        "new_notif_count" to 0
                                    )

                                    db.collection("user").document(user?.uid.toString())
                                        .set(userInfo, SetOptions.merge())
                                        .addOnSuccessListener {
                                            Log.d(
                                                "SignupSellerFragment.createUser()",
                                                "user cloud firestore 등록 완료\n authUID: ${user?.uid}"
                                            )
                                            showSnackBar("회원가입 성공했습니다.")
                                            findNavController().navigate(R.id.action_signupSellerFragment_to_loginFragment)
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e(
                                                "FirebaseException",
                                                "user cloud firestore 등록 실패: ${e.stackTrace}",
                                                e
                                            )
                                        }
                                }
                        } else {
                            showSnackBar("회원가입 실패했습니다.")
                        }
                    } else {
                        showSnackBar("이메일 형식이 아닙니다.")
                        binding.run {
                            editinputSignupSellerEmail.requestFocus()
                            editinputSignupSellerEmail.setText("")
                        }
                    }
                }
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setAnchorView(binding.buttonSignupSellerComplete)
            .show()
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