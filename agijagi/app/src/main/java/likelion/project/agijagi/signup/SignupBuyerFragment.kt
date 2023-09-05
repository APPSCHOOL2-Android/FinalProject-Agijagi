package likelion.project.agijagi.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestoreSettings
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentSignupBuyerBinding
import java.lang.Error
import java.util.regex.Pattern

class SignupBuyerFragment : Fragment() {

    private var _fragmentSignupBuyerBinding: FragmentSignupBuyerBinding? = null
    private val fragmentSignupBuyerBinding get() = _fragmentSignupBuyerBinding!!

    private val signupViewModel by lazy { ViewModelProvider(this).get(SignupBuyerViewModel::class.java) }

    private var auth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentSignupBuyerBinding = FragmentSignupBuyerBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        setDB()
        Log.d("view","onCreateView")
        return fragmentSignupBuyerBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("view","onViewCreated")

       fragmentSignupBuyerBinding.run {
            toolbarSignupSellerToolbar.setNavigationOnClickListener {
                findNavController().navigate(R.id.action_signupBuyerFragment_to_signupSelectFragment)
            }

            obserViewModel()
           Log.d("view","onbserViewModel() 함수 실행")

            // 이름
            editinputSignupBuyerName.addTextChangedListener { name ->
                signupViewModel.setName(
                    if(name != null) name.toString() else ""
                )
                Log.d("view","이름 text 실행")
            }

           editinputSignupBuyerName.setOnEditorActionListener { textView, i, keyEvent ->


               false
           }

/*
            // 닉네임
            editinputSignupBuyerNickname.doAfterTextChanged { nickName ->
                signupViewModel.setNickName(
                    if(nickName != null) nickName.toString() else "고길동"
                )
            }

            // 이메일
            editinputSignupBuyerEmail.doAfterTextChanged { email ->
                signupViewModel.setEmail(
                    if(email != null) email.toString() else ""
                )
            }

            // 패스워드
            editinputSignupBuyerPassword.doAfterTextChanged { password ->
                signupViewModel.setPassword(
                    if(password != null) password.toString() else ""
                )
            }

            // 비밀번호 재입력 확인
            editinputSignupBuyerCheckPassword.doAfterTextChanged { passwordCheck ->
                signupViewModel.setPasswordCheck(
                    if(passwordCheck != null) passwordCheck.toString() else ""
                )
            }

            buttonSignupBuyerComplete.setOnClickListener {
                createUser(
                    name = signupViewModel.name.value!!,
                    nickname = signupViewModel.nickname.value!!,
                    email = signupViewModel.email.value!!,
                    password = signupViewModel.password.value!!
                )
            }*/
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("onDestroyView","error")
        _fragmentSignupBuyerBinding = null
    }

    fun obserViewModel(){
        // 뷰가 살아있을 때만 lifeCycle 동작
        signupViewModel.name.observe(viewLifecycleOwner) { name ->
            // stackoverflow 가 뜨면 viewmodel 에서 다를 경우에만 setText 함수 호출
            fragmentSignupBuyerBinding.editinputSignupBuyerName.setText(name)
        }

        signupViewModel.nickname.observe(viewLifecycleOwner) { nickname ->
            fragmentSignupBuyerBinding.editinputSignupBuyerNickname.setText(nickname)
        }

        signupViewModel.email.observe(viewLifecycleOwner) { email ->
            fragmentSignupBuyerBinding.editinputSignupBuyerEmail.setText(email)
        }

        signupViewModel.password.observe(viewLifecycleOwner) { password ->
            fragmentSignupBuyerBinding.editinputSignupBuyerPassword.setText(password)
        }

        signupViewModel.buttonState.observe(viewLifecycleOwner) { buttonState ->
            if(buttonState) {
                fragmentSignupBuyerBinding.buttonSignupBuyerComplete.isSelected = true
                fragmentSignupBuyerBinding.buttonSignupBuyerComplete.setTextColor(resources.getColor(R.color.white))
            } else {
                fragmentSignupBuyerBinding.buttonSignupBuyerComplete.isSelected = false
                fragmentSignupBuyerBinding.buttonSignupBuyerComplete.setTextColor(resources.getColor(R.color.jagi_hint_color))
            }
        }
    }
    private fun createUser(name: String, nickname: String, email: String, password: String) {
        auth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { task ->
                // 이메일 형식 체크
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (task.isSuccessful) {
                        if (_fragmentSignupBuyerBinding != null) {
                            var snackbar = Snackbar.make(
                                fragmentSignupBuyerBinding.root,
                                "회원가입 성공",
                                Snackbar.LENGTH_SHORT
                            )
                            snackbar.show()
                        }

                        val userInformation = hashMapOf(
                            "name" to name,
                            "nickname" to nickname,
                            "email" to email,
                            "password" to password
                        )

                        db.collection(email).document("Info")
                            .set(userInformation, SetOptions.merge())

                        findNavController().navigate(R.id.action_signupBuyerFragment_to_loginFragment)

                    } else {
                        var snackbar = Snackbar.make(
                            fragmentSignupBuyerBinding.root,
                            "회원가입 실패",
                            Snackbar.LENGTH_SHORT
                        )
                        snackbar.show()
                    }
                } else {
                    var snackbar = Snackbar.make(
                        fragmentSignupBuyerBinding.root,
                        "이메일 형식이 아닙니다.",
                        Snackbar.LENGTH_SHORT
                    )
                    snackbar.show()
                }
            }
    }

    fun setDB() {
        db = FirebaseFirestore.getInstance()
        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
    }
}