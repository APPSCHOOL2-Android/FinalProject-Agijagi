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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentLoginBinding
import likelion.project.agijagi.model.BuyerModel
import likelion.project.agijagi.model.SellerModel
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
                findNavController().popBackStack()
            }

            // 구글로그인으로 시작 클릭 시
            buttonLoginGoogleloginbutton.setOnClickListener {
                signInWithGoogle()
            }

            // 로그인 클릭 시 ( 자체 앱 )
            buttonLoginJagilogin.setOnClickListener {

                if (editinputLoginEmail.text.toString()
                        .isNotBlank() && editinputLoginPassword.text.toString().isNotBlank()
                ) {
                    createUser(
                        email = editinputLoginEmail.text.toString().trim(),
                        password = editinputLoginPassword.text.toString().trim()
                    )
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

    private fun createUser(email: String, password: String) {
        auth?.signInWithEmailAndPassword(
            email, password
        )?.addOnCompleteListener(requireActivity()) {
            if (it.isSuccessful) {

                db.collection("user").document(auth?.currentUser?.uid.toString()).get()
                    .addOnSuccessListener { user ->

                        if (user["is_seller"] == false) {
                            // auth?.currentUser?.uid.toString() == it.id 같은 값을 가진다
                            Log.d("getid", "get Uid: ${auth?.currentUser?.uid.toString()}")
                            showSnackBar("로그인에 성공하였습니다.")

                            UserModel.uid = auth?.currentUser?.uid.toString()
                            UserModel.email = user["email"].toString()
                            UserModel.emailNotif = user.getBoolean("emailNotif")
                            UserModel.googleLoginCheck = user.getBoolean("google_login_check")
                            UserModel.isSeller = user.getBoolean("is_seller")
                            UserModel.name = user["name"].toString()
                            UserModel.newChatCount = user["new_chat_count"].toString().toInt()
                            UserModel.newNotifCount = user["new_notif_count"].toString().toInt()
                            UserModel.password = user["password"].toString()
                            UserModel.roleId = user["role_id"].toString()
                            UserModel.smsNotif = user.getBoolean("sms_notif")

                            db.collection("buyer").document(user["role_id"].toString()).get()
                                .addOnSuccessListener { buyerdocument ->

                                    BuyerModel.basic = buyerdocument["basic"].toString()
                                    BuyerModel.nickname = buyerdocument["nickname"].toString()

                                    // "notif_setting" 필드가 있는지 확인하고 값을 가져옵니다.
                                    val notifSettingField = buyerdocument["notif_setting"]
                                    if (notifSettingField is Map<*, *>) {
                                        // "notif_setting" 필드가 Map으로 저장되어 있을 경우, MutableMap<String, Boolean>으로 변환합니다.
                                        val notifSettingMap = mutableMapOf<String, Boolean>()
                                        for (entry in notifSettingField) {
                                            if (entry.key is String && entry.value is Boolean) {
                                                notifSettingMap[entry.key.toString()] =
                                                    entry.value.toString().toBoolean()
                                            }
                                        }
                                        BuyerModel.notifSetting = notifSettingMap

                                    } else {
                                        // "notif_setting" 필드가 없거나 유효한 Map이 아닌 경우 기본값 또는 오류 처리를 수행합니다.
                                        // 예를 들어, 기본값으로 빈 Map을 할당할 수 있습니다.
                                        BuyerModel.notifSetting = mutableMapOf()
                                    }
                                    findNavController().popBackStack()
                                }

                        } else if (user["is_seller"] == true) {
                            Log.d("getid", "getid: ${user.id}")
                            showSnackBar("로그인에 성공하였습니다.")

                            UserModel.uid = auth?.currentUser?.uid.toString()
                            UserModel.email = user["email"].toString()
                            UserModel.emailNotif = user.getBoolean("emailNotif")
                            UserModel.googleLoginCheck = user.getBoolean("google_login_check")
                            UserModel.isSeller = user.getBoolean("is_seller")
                            UserModel.name = user["name"].toString()
                            UserModel.newChatCount = user["new_chat_count"].toString().toInt()
                            UserModel.newNotifCount = user["new_notif_count"].toString().toInt()
                            UserModel.password = user["password"].toString()
                            UserModel.roleId = user["role_id"].toString()
                            UserModel.smsNotif = user.getBoolean("sms_notif")

                            db.collection("seller").document(user["role_id"].toString()).get()
                                .addOnSuccessListener { sellerdocument ->

                                    SellerModel.sellerId = user["role_id"].toString()
                                    SellerModel.address = sellerdocument["address"].toString()
                                    SellerModel.brCert = sellerdocument["br_cert"].toString()
                                    SellerModel.brn = sellerdocument["brn"].toString()
                                    SellerModel.businessName =
                                        sellerdocument["bussiness_name"].toString()
                                    SellerModel.tel = sellerdocument["tel"].toString()

                                    // "notif_setting" 필드가 있는지 확인하고 값을 가져옵니다.
                                    val notifSettingField = sellerdocument["notif_setting"]
                                    if (notifSettingField is Map<*, *>) {
                                        // "notif_setting" 필드가 Map으로 저장되어 있을 경우, MutableMap<String, Boolean>으로 변환합니다.
                                        val notifSettingMap = mutableMapOf<String, Boolean>()
                                        for (entry in notifSettingField) {
                                            if (entry.key is String && entry.value is Boolean) {
                                                notifSettingMap[entry.key.toString()] =
                                                    entry.value.toString().toBoolean()
                                            }
                                        }
                                        SellerModel.notifSetting = notifSettingMap

                                    } else {
                                        // "notif_setting" 필드가 없거나 유효한 Map이 아닌 경우 기본값 또는 오류 처리를 수행합니다.
                                        // 예를 들어, 기본값으로 빈 Map을 할당할 수 있습니다.
                                        SellerModel.notifSetting = mutableMapOf()
                                    }
                                    findNavController().navigate(R.id.action_loginFragment_to_sellerMypageFragment)
                                }
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
                    Log.d("login", "error: ApiException")
                    showSnackBar("로그인에 실패하였습니다.")
                }
            } else {
                Log.d("login", "error: requestCode Error")
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
                    Log.d("login", "error: task  is fail")
                    showSnackBar("로그인에 실패하였습니다.")
                }
            }
    }

    private fun checkUserInfoInFirestore(user: FirebaseUser?) {
        if (user != null) {

            db.collection("user")
                // 문서 확인을 해야할 듯
                .document(user.uid)
                .get()
                .addOnSuccessListener { user ->

                    if (user["name"].toString().length >= 2 && user["is_seller"] == false) {
                        UserModel.uid = auth?.currentUser?.uid.toString()
                        UserModel.email = user["email"].toString()
                        UserModel.emailNotif = user.getBoolean("emailNotif")
                        UserModel.googleLoginCheck = user.getBoolean("google_login_check")
                        UserModel.isSeller = user.getBoolean("is_seller")
                        UserModel.name = user["name"].toString()
                        UserModel.newChatCount = user["new_chat_count"].toString().toInt()
                        UserModel.newNotifCount = user["new_notif_count"].toString().toInt()
                        UserModel.password = user["password"].toString()
                        UserModel.roleId = user["role_id"].toString()
                        UserModel.smsNotif = user.getBoolean("sms_notif")

                        db.collection("buyer").document(user["role_id"].toString()).get()
                            .addOnSuccessListener { buyerdocument ->

                                BuyerModel.basic = buyerdocument["basic"].toString()
                                BuyerModel.nickname = buyerdocument["nickname"].toString()

                                // "notif_setting" 필드가 있는지 확인하고 값을 가져옵니다.
                                val notifSettingField = buyerdocument["notif_setting"]
                                if (notifSettingField is Map<*, *>) {
                                    // "notif_setting" 필드가 Map으로 저장되어 있을 경우, MutableMap<String, Boolean>으로 변환합니다.
                                    val notifSettingMap = mutableMapOf<String, Boolean>()
                                    for (entry in notifSettingField) {
                                        if (entry.key is String && entry.value is Boolean) {
                                            notifSettingMap[entry.key.toString()] =
                                                entry.value.toString().toBoolean()
                                        }
                                    }
                                    BuyerModel.notifSetting = notifSettingMap

                                } else {
                                    // "notif_setting" 필드가 없거나 유효한 Map이 아닌 경우 기본값 또는 오류 처리를 수행합니다.
                                    // 예를 들어, 기본값으로 빈 Map을 할당할 수 있습니다.
                                    BuyerModel.notifSetting = mutableMapOf()
                                }
                            }
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                        showSnackBar("로그인에 성공하셨습니다.")

                    } else if (user["name"].toString().length >= 2 && user["is_seller"] == true) {
                        UserModel.uid = auth?.currentUser?.uid.toString()
                        UserModel.email = user["email"].toString()
                        UserModel.emailNotif = user.getBoolean("emailNotif")
                        UserModel.googleLoginCheck = user.getBoolean("google_login_check")
                        UserModel.isSeller = user.getBoolean("is_seller")
                        UserModel.name = user["name"].toString()
                        UserModel.newChatCount = user["new_chat_count"].toString().toInt()
                        UserModel.newNotifCount = user["new_notif_count"].toString().toInt()
                        UserModel.password = user["password"].toString()
                        UserModel.roleId = user["role_id"].toString()
                        UserModel.smsNotif = user.getBoolean("sms_notif")

                        db.collection("seller").document(user["role_id"].toString()).get()
                            .addOnSuccessListener { sellerdocument ->

                                SellerModel.sellerId = user["role_id"].toString()
                                SellerModel.address = sellerdocument["address"].toString()
                                SellerModel.brCert = sellerdocument["br_cert"].toString()
                                SellerModel.brn = sellerdocument["brn"].toString()
                                SellerModel.businessName =
                                    sellerdocument["bussiness_name"].toString()
                                SellerModel.tel = sellerdocument["tel"].toString()

                                // "notif_setting" 필드가 있는지 확인하고 값을 가져옵니다.
                                val notifSettingField = sellerdocument["notif_setting"]
                                if (notifSettingField is Map<*, *>) {
                                    // "notif_setting" 필드가 Map으로 저장되어 있을 경우, MutableMap<String, Boolean>으로 변환합니다.
                                    val notifSettingMap = mutableMapOf<String, Boolean>()
                                    for (entry in notifSettingField) {
                                        if (entry.key is String && entry.value is Boolean) {
                                            notifSettingMap[entry.key.toString()] =
                                                entry.value.toString().toBoolean()
                                        }
                                    }
                                    SellerModel.notifSetting = notifSettingMap

                                } else {
                                    // "notif_setting" 필드가 없거나 유효한 Map이 아닌 경우 기본값 또는 오류 처리를 수행합니다.
                                    // 예를 들어, 기본값으로 빈 Map을 할당할 수 있습니다.
                                    SellerModel.notifSetting = mutableMapOf()
                                }
                                findNavController().navigate(R.id.action_loginFragment_to_sellerMypageFragment)
                                showSnackBar("로그인에 성공하셨습니다.")
                            }
                    } else {
                        // 정보가 없는 경우 SignupSelectFragment로 이동
                        findNavController().navigate(R.id.action_loginFragment_to_signupSelectFragment)
                    }
                }.addOnFailureListener {
                    showSnackBar("로그인에 실패")
                }
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