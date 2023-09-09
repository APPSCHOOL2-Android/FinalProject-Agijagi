package likelion.project.agijagi.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _fragmentLoginBinding: FragmentLoginBinding? = null
    private val fragmentLoginBinding
        get() = _fragmentLoginBinding!!

    private var auth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false)

        return fragmentLoginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        setup()


        fragmentLoginBinding.run {
            // 구글로그인으로 시작 클릭 시
            buttonLoginGoogleloginbutton.setOnClickListener {
                Snackbar.make(requireView(), "아직 개발 중 입니다.", Toast.LENGTH_SHORT).show()
            }

            // 로그인 클릭 시 ( 자체 앱 )
            buttonLoginJagilogin.setOnClickListener {
                auth?.signInWithEmailAndPassword(
                    editinputLoginEmail.text.toString().trim(),
                    editinputLoginPassword.text.toString().trim()
                )?.addOnCompleteListener(requireActivity()) {
                    if (it.isSuccessful) {
                        // collection(user).document(email).get(is_seller): true -> seller , false -> buyer
                        db.collection("user").document(editinputLoginEmail.text.toString()).get()
                            .addOnSuccessListener {
                                if (it["is_seller"] == true) {
                                    findNavController().navigate(R.id.action_loginFragment_to_sellerMypageFragment)
                                    Snackbar.make(view, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                                } else {
                                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                                    Snackbar.make(view, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Snackbar.make(view, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // 회원 가입 버튼 클릭 시
            textviewLoginJointextview.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_signupSelectFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentLoginBinding = null
    }

    private fun setup() {
        db = Firebase.firestore

        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
    }
}