package likelion.project.agijagi.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentLoginBinding
import likelion.project.agijagi.model.UserModel

class LoginFragment : Fragment() {
    companion object {
        const val RC_SIGN_IN = 100 // 원하는 값으로 설정할 수 있음
    }

    private var _fragmentLoginBinding: FragmentLoginBinding? = null
    private val fragmentLoginBinding
        get() = _fragmentLoginBinding!!

    private var auth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore

    private var buttonState = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false)

        return fragmentLoginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        setup()

        fragmentLoginBinding.run {
            toolbarLogin.setNavigationOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }

            // 구글로그인으로 시작 클릭 시
            buttonLoginGoogleloginbutton.setOnClickListener {
                signInWithGoogle()
            }

            // 로그인 클릭 시 ( 자체 앱 )
            buttonLoginJagilogin.setOnClickListener {

                if(editinputLoginEmail.text.toString().isNotBlank() && editinputLoginPassword.text.toString().isNotBlank()){
                    createUser(
                        email = editinputLoginEmail.text.toString().trim(),
                        password = editinputLoginPassword.text.toString().trim())
                } else {
                    showSnackBar("정보를 기입해주세요.")
                }
            }

            // 회원 가입 버튼 클릭 시
            textviewLoginJointextview.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_signupSelectFragment)
            }
        }
    }

    private fun createUser(email: String, password: String){
        auth?.signInWithEmailAndPassword(
            email,password
        )?.addOnCompleteListener(requireActivity()) {
            if (it.isSuccessful) {

                db.collection("user").document(auth?.currentUser?.uid.toString()).get()
                    .addOnSuccessListener {

                        if (it["is_seller"] == false) {
                            // auth?.currentUser?.uid.toString() == it.id 같은 값을 가진다
                            Log.d("getid", "get Uid: ${auth?.currentUser?.uid.toString()}")
                            showSnackBar("로그인에 성공하였습니다.")

                            MainActivity.userModel.uid = auth?.currentUser?.uid.toString()
                            MainActivity.userModel.email = it["email"].toString()
                            MainActivity.userModel.emailNotif = it.getBoolean("emailNotif")
                            MainActivity.userModel.googleLoginCheck = it.getBoolean("google_login_check")
                            MainActivity.userModel.isSeller = it.getBoolean("is_seller")
                            MainActivity.userModel.name = it["name"].toString()
                            MainActivity.userModel.newChatCount = it["new_chat_count"].toString().toInt()
                            MainActivity.userModel.newNotifCount = it["new_notif_count"].toString().toInt()
                            MainActivity.userModel.password =  it["password"].toString()
                            MainActivity.userModel.smsNotif = it.getBoolean("sms_notif")

                            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)

                        } else if(it["is_seller"] == true){
                            Log.d("getid", "getid: ${it.id}")
                            showSnackBar("로그인에 성공하였습니다.")

                            MainActivity.userModel.uid = auth?.currentUser?.uid.toString()
                            MainActivity.userModel.email = it["email"].toString()
                            MainActivity.userModel.emailNotif = it.getBoolean("emailNotif")
                            MainActivity.userModel.googleLoginCheck = it.getBoolean("google_login_check")
                            MainActivity.userModel.isSeller = it.getBoolean("is_seller")
                            MainActivity.userModel.name = it["name"].toString()
                            MainActivity.userModel.newChatCount = it["new_chat_count"] as Int?
                            MainActivity.userModel.newNotifCount = it["new_notif_count"] as Int?
                            MainActivity.userModel.password =  it["password"].toString()
                            MainActivity.userModel.smsNotif = it.getBoolean("sms_notif")

                            findNavController().navigate(R.id.action_loginFragment_to_sellerMypageFragment)
                        }
                    }
            } else {
                showSnackBar("로그인에 실패하였습니다.")
            }
        }?.addOnFailureListener {
            showSnackBar("로그인에 실패하였습니다.")
        }
    }


    private fun signInWithGoogle() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)

//        googleSignInClient.signOut()

        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account)
                } catch (e: ApiException) {
                    Log.d("login","error: ApiException")
                    showSnackBar("로그인에 실패하였습니다.")
                }
            } else {
                Log.d("login","error: requestCode Error")
                showSnackBar("로그인에 실패하였습니다.")
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth!!.currentUser
                    // Firebase Firestore에서 정보 확인
                    checkUserInfoInFirestore(user)
                } else {
                    // 로그인 실패 처리
                    Log.d("login","error: task  is fail")
                    showSnackBar("로그인에 실패하였습니다.")
                }
            }
    }

    private fun checkUserInfoInFirestore(user: FirebaseUser?) {
        if (user != null) {
            db.collection("users")
                // 문서 확인을 해야할 듯
                .document(user.uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    // name 이 널이 아니고 is_seller true or false 로 구분해야하는가 ?
                    if (documentSnapshot["name"].toString().length >= 2 && documentSnapshot["is_seller"] == false) {
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)

                        MainActivity.userModel.uid = auth?.currentUser?.uid.toString()
                        MainActivity.userModel.email = documentSnapshot["email"].toString()
                        MainActivity.userModel.emailNotif = documentSnapshot.getBoolean("emailNotif")
                        MainActivity.userModel.googleLoginCheck = documentSnapshot.getBoolean("google_login_check")
                        MainActivity.userModel.isSeller = documentSnapshot.getBoolean("is_seller")
                        MainActivity.userModel.name = documentSnapshot["name"].toString()
                        MainActivity.userModel.newChatCount = documentSnapshot["new_chat_count"].toString().toInt()
                        MainActivity.userModel.newNotifCount = documentSnapshot["new_notif_count"].toString().toInt()
                        MainActivity.userModel.password =  documentSnapshot["password"].toString()
                        MainActivity.userModel.smsNotif = documentSnapshot.getBoolean("sms_notif")

                        showSnackBar("로그인에 성공하셨습니다.")
                    } else if (documentSnapshot["name"].toString().length >= 2 && documentSnapshot["is_seller"] == true) {
                        findNavController().navigate(R.id.action_loginFragment_to_sellerMypageFragment)

                        MainActivity.userModel.uid = auth?.currentUser?.uid.toString()
                        MainActivity.userModel.email = documentSnapshot["email"].toString()
                        MainActivity.userModel.emailNotif = documentSnapshot.getBoolean("emailNotif")
                        MainActivity.userModel.googleLoginCheck = documentSnapshot.getBoolean("google_login_check")
                        MainActivity.userModel.isSeller = documentSnapshot.getBoolean("is_seller")
                        MainActivity.userModel.name = documentSnapshot["name"].toString()
                        MainActivity.userModel.newChatCount = documentSnapshot["new_chat_count"].toString().toInt()
                        MainActivity.userModel.newNotifCount = documentSnapshot["new_notif_count"].toString().toInt()
                        MainActivity.userModel.password =  documentSnapshot["password"].toString()
                        MainActivity.userModel.smsNotif = documentSnapshot.getBoolean("sms_notif")

                        showSnackBar("로그인에 성공하셨습니다.")
                    }
                    else {
                        // 정보가 없는 경우 SignupSelectFragment로 이동
                        findNavController().navigate(R.id.action_loginFragment_to_signupSelectFragment)
                }
            }.addOnFailureListener {
                    showSnackBar("로그인에 실패") }
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(fragmentLoginBinding.root, message, Toast.LENGTH_SHORT).show()
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