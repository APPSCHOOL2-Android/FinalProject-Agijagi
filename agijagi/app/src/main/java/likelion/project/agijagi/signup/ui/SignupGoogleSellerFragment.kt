package likelion.project.agijagi.signup.ui

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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentGoogleSignupSellerBinding

class SignupGoogleSellerFragment : Fragment() {

    private var _fragmentGoogleSignupSellerBinding: FragmentGoogleSignupSellerBinding? = null
    private val fragmentGoogleSignupSellerBinding get() = _fragmentGoogleSignupSellerBinding!!

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

        _fragmentGoogleSignupSellerBinding = FragmentGoogleSignupSellerBinding.inflate(inflater, container, false)

        return fragmentGoogleSignupSellerBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        setup()

        fragmentGoogleSignupSellerBinding.run {
            toolbarGoogleSignupSellerToolbar.setNavigationOnClickListener {
                findNavController().navigate(R.id.action_signupGoogleSellerFragment_to_signupSelectFragment)
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
                if(buttonState == true) {
                    createUser()
                    showSnackBar("회원가입 성공했습니다.")
                }
                else {
                    showSnackBar("회원가입 실패했습니다.")
                }
            }
        }

    }

    private fun showSnackBar(message: String){
        Snackbar.make(fragmentGoogleSignupSellerBinding.root, message, Toast.LENGTH_SHORT).show()
    }
    private fun isValidSignupButton(): Boolean {
        buttonState = businessNameState && representativeNameState && registrationNumberState &&
                 businessAddressState && businessNumberState
        return buttonState
    }

    private fun setSignupButtonState(state: Boolean){
        if(state) {
            fragmentGoogleSignupSellerBinding.buttonGoogleSignupSellerComplete.isSelected = true
            fragmentGoogleSignupSellerBinding.buttonGoogleSignupSellerComplete.setTextColor(resources.getColor(R.color.white))
        } else {
            fragmentGoogleSignupSellerBinding.buttonGoogleSignupSellerComplete.isSelected = false
            fragmentGoogleSignupSellerBinding.buttonGoogleSignupSellerComplete.setTextColor(resources.getColor(R.color.jagi_hint_color))

        }
    }

    private fun createUser() {
        val sellerSetting = hashMapOf(
            "exchange" to false,
            "inquiry" to false,
            "order" to false
        )

        val sellerInfo = hashMapOf(
            "address" to fragmentGoogleSignupSellerBinding.editinputGoogleSignupSellerBusinessAddress.text.toString(),
            "br_cert" to "",
            "brn" to fragmentGoogleSignupSellerBinding.editinputGoogleSignupSellerRegistrationNumber.text.toString(),
            "bussiness_name" to fragmentGoogleSignupSellerBinding.editinputGoogleSignupSellerBusinessName.text.toString(),
            "notif_setting" to sellerSetting,
            "tel" to fragmentGoogleSignupSellerBinding.editinputGoogleSignupSellerBusinessNumber.text.toString()
        )

        db.collection("seller").add(sellerInfo)
            .addOnSuccessListener {
            roleId = it.id

                val userInfo = hashMapOf(
                    "email" to mAuth?.email.toString(),
                    "password" to "",
                    "name" to fragmentGoogleSignupSellerBinding.editinputGoogleSignupSellerRepresentativeName.text.toString(),
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
                    .addOnSuccessListener { Log.d("firebase", "user cloud firestore 등록 완료\n" +
                            " authUID: ${mAuth?.uid}")
                        findNavController().navigate(R.id.action_signupGoogleSellerFragment_to_loginFragment)}
                    .addOnFailureListener { e -> Log.w("firebase", "user cloud firestore 등록 실패", e)  }
            }
            .addOnFailureListener {
                showSnackBar("회원가입 실패")
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentGoogleSignupSellerBinding = null
    }

    private fun setup() {
        db = Firebase.firestore

        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
    }
}