package likelion.project.agijagi.buyermypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentProfileManagementBinding
import likelion.project.agijagi.model.BuyerModel
import likelion.project.agijagi.model.UserModel

class ProfileManagementFragment : Fragment() {

    private var _binding: FragmentProfileManagementBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileManagementBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setup()

        setDefaultValues()
        setToolbarItemAction()
        setEditButton()
    }

    private fun setToolbarItemAction() {
        binding.toolbarProfileManagement.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setDefaultValues() {
        binding.run {
            textviewProfileManagementEmail.text = UserModel.email
            editInputProfileManagementNickname.hint = BuyerModel.nickname
            checkboxProfileManagementSms.isChecked = UserModel.smsNotif == true
            checkboxProfileManagementEmail.isChecked = UserModel.emailNotif == true

            if (UserModel.googleLoginCheck == true) {
                editInputProfileManagementName.setText(UserModel.name)
                editInputProfileManagementName.isFocusable = false
                layoutNoGoogleId.visibility = View.GONE
            } else { // null, false
                editInputProfileManagementName.hint = UserModel.name
                editInputProfileManagementName.setText("")
                editInputProfileManagementName.isFocusable = true
                layoutNoGoogleId.visibility = View.VISIBLE
            }
        }
    }

    private fun setEditButton() {
        binding.run {
            buttonProfileManagementEdit.setOnClickListener {
                // 유효성 검사
                val name = editInputProfileManagementName.text.toString()
                val nickname = editInputProfileManagementNickname.text.toString()
                val pw = editInputProfileManagementPassword.text.toString()
                val pwCk = editInputProfileManagementCheckPassword.text.toString()
                val smsNotif = checkboxProfileManagementSms.isChecked
                val emailNotif = checkboxProfileManagementEmail.isChecked

                val userData = mutableMapOf<String, Any>()

                if (UserModel.googleLoginCheck != true) {
                    if (pw.length < 6) {
                        Snackbar.make(binding.root, "비밀번호를 6자리 이상 입력하세요", Snackbar.LENGTH_SHORT)
                            .setAnchorView(buttonProfileManagementEdit)
                            .setTextColor(resources.getColor(R.color.jagi_carrot))
                            .show()
                        return@setOnClickListener
                    }
                    if (pw != pwCk) {
                        Snackbar.make(binding.root, "비밀번호가 다릅니다!", Snackbar.LENGTH_SHORT)
                            .setAnchorView(buttonProfileManagementEdit)
                            .setTextColor(resources.getColor(R.color.jagi_carrot))
                            .show()
                        return@setOnClickListener
                    }
                    UserModel.password = pw
                    userData["password"] = pw

                    if (name == "" || name == " ") {
                    } else {
                        UserModel.name = name
                        userData["name"] = name
                    }
                }

                // 공통
                if (nickname == "" || nickname == " ") {
                } else {
                    BuyerModel.nickname = nickname
                    db.collection("buyer").document(UserModel.roleId)
                        .update("nickname", BuyerModel.nickname)
                }
                UserModel.smsNotif = smsNotif
                userData["smsNotif"] = smsNotif
                UserModel.emailNotif = emailNotif
                userData["emailNotif"] = emailNotif

                // 서버 저장
                db.collection("user").document(UserModel.uid).update(userData)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            // 수정 알림과 이동
                            // 화면 터치 막기
                            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            Snackbar.make(binding.root, "수정되었습니다", Snackbar.LENGTH_SHORT)
                                .setAnchorView(buttonProfileManagementEdit)
                                .addCallback(object : Snackbar.Callback() {
                                    // 스낵바가 종료될 때의 처리
                                    override fun onDismissed(
                                        transientBottomBar: Snackbar?,
                                        event: Int
                                    ) {
                                        super.onDismissed(transientBottomBar, event)
                                        // 화면 터치 풀기
                                        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                        // 화면이동
                                        findNavController().popBackStack()
                                    }
                                })
                                .show()
                        } else {
                            Log.e("FirebaseException", it.exception.toString())
                        }
                    }
            }
        }
    }

    fun setup() {
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