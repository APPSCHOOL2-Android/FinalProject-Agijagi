package likelion.project.agijagi.signup

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import likelion.project.agijagi.databinding.FragmentSignupBuyerBinding
import java.util.regex.Pattern

class SignupBuyerFragment : Fragment() {

    private var _fragmentSignupBuyerBinding: FragmentSignupBuyerBinding? = null
    private val fragmentSignupBuyerBinding get() = _fragmentSignupBuyerBinding!!

    private var auth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore

    private val emailValidation =
        "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

    private var nameState = false
    private var nickNameState = false
    private var emailState = false
    private var passWordState = false

    private var buttonState = false

    private var roleId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentSignupBuyerBinding = FragmentSignupBuyerBinding.inflate(inflater, container, false)

        return fragmentSignupBuyerBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        setup()

        fragmentSignupBuyerBinding.run {
            toolbarSignupSellerToolbar.setNavigationOnClickListener {
                findNavController().navigate(R.id.action_signupBuyerFragment_to_signupSelectFragment)
            }

            // 이름
            editinputSignupBuyerName.doAfterTextChanged { name ->
                nameState = name.toString().length in 2..4
                setSignupButtonState(isValidSignupButton())
            }

            // 닉네임
            editinputSignupBuyerNickname.doAfterTextChanged { nickName ->
                nickNameState = nickName.toString().length in 2..9
                setSignupButtonState(isValidSignupButton())
            }

            // 이메일
            editinputSignupBuyerEmail.doAfterTextChanged { email ->
                emailState = Pattern.matches(emailValidation, email)
                setSignupButtonState(isValidSignupButton())
            }

            // 패스워드
            editinputSignupBuyerPassword.doAfterTextChanged { password ->
                isValiedPassWord()
                setSignupButtonState(isValidSignupButton())
            }

            // 비밀번호 재입력 확인
            editinputSignupBuyerCheckPassword.doAfterTextChanged { passwordCheck ->
                isValiedPassWord()
                setSignupButtonState(isValidSignupButton())
            }

            buttonSignupBuyerComplete.setOnClickListener {
                createUser(
                    email = editinputSignupBuyerEmail.text.toString(),
                    password = editinputSignupBuyerPassword.text.toString()
                )
            }
        }
    }

    private fun isValiedPassWord(){
        fragmentSignupBuyerBinding.run {
            passWordState = editinputSignupBuyerPassword.text.toString() == editinputSignupBuyerCheckPassword.text.toString() &&
                    editinputSignupBuyerPassword.text.toString().isNotBlank() &&
                    editinputSignupBuyerCheckPassword.text.toString().isNotBlank() &&
                    editinputSignupBuyerPassword.text.toString().length in (6..15) &&
                    editinputSignupBuyerCheckPassword.text.toString().length in (6..15)
        }
    }

    private fun isValidSignupButton(): Boolean {
        buttonState = nameState && nickNameState && emailState && passWordState
        return buttonState
    }

    private fun setSignupButtonState(state: Boolean){
        if(state) {
            fragmentSignupBuyerBinding.buttonSignupBuyerComplete.isSelected = true
            fragmentSignupBuyerBinding.buttonSignupBuyerComplete.setTextColor(resources.getColor(R.color.white))
        } else {
            fragmentSignupBuyerBinding.buttonSignupBuyerComplete.isSelected = false
            fragmentSignupBuyerBinding.buttonSignupBuyerComplete.setTextColor(resources.getColor(R.color.jagi_hint_color))

        }
    }

    private fun createUser(email: String, password: String) {
        if(_fragmentSignupBuyerBinding != null) {

            auth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener { task ->
                    // 이메일 형식 체크
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        if (task.isSuccessful) {
                            val user = auth?.currentUser

                            val notifSetting = hashMapOf(
                                "cs_alert" to false,
                                "delivery_alert" to false,
                                "making_alert" to false
                            )

                            val buyerInfo = hashMapOf(
                                "nickname" to fragmentSignupBuyerBinding.editinputSignupBuyerNickname.text.toString(),
                                "notif_setting" to notifSetting,
                                "user_id" to user?.uid.toString()
                            )

                            db.collection("buyer")
                                .add(buyerInfo)
                                .addOnSuccessListener { documentReference ->
                                    roleId = documentReference.id
                                    Log.d("buyerFragment", "roleID: ${roleId}")

                                    val userInfo = hashMapOf(
                                        "email" to email,
                                        "password" to password,
                                        "name" to fragmentSignupBuyerBinding.editinputSignupBuyerName.text.toString(),
                                        "google_login_check" to false,
                                        "email_notif" to false,
                                        "sms_notif" to false,
                                        "is_seller" to false,
                                        "role_id" to roleId,
                                        "new_chat_count" to 0,
                                        "new_notif_count" to 0
                                    )

                                    db.collection("user").document(user?.uid.toString())
                                        .set(userInfo, SetOptions.merge())
                                        .addOnSuccessListener {
                                            Log.d(
                                                "firebase",
                                                "user cloud firestore 등록 완료\n authUID: ${user?.uid}"
                                            )
                                            Snackbar.make(fragmentSignupBuyerBinding.root, "회원가입 성공했습니다.", Toast.LENGTH_SHORT).show()
                                            findNavController().navigate(R.id.action_signupBuyerFragment_to_loginFragment)
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(
                                                "firebase",
                                                "user cloud firestore 등록 실패",
                                                e
                                            )
                                        }
                                }

                        } else {
                            Snackbar.make(fragmentSignupBuyerBinding.root, "회원가입 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Snackbar.make(fragmentSignupBuyerBinding.root, "이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show()
                        fragmentSignupBuyerBinding.run {
                            editinputSignupBuyerEmail.requestFocus()
                            editinputSignupBuyerEmail.setText("")
                        }
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentSignupBuyerBinding = null
    }

    private fun setup() {
        db = Firebase.firestore

        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
    }
}