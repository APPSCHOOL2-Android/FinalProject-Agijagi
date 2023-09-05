package likelion.project.agijagi.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _fragmentLoginBinding: FragmentLoginBinding? = null
    private val fragmentLoginBinding
        get() = _fragmentLoginBinding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false)

        return fragmentLoginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentLoginBinding.run {
            // 구글로그인으로 시작 클릭 시
            buttonLoginGoogleloginbutton.setOnClickListener {

            }

            // 로그인 클릭 시 ( 자체 앱 )

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
}