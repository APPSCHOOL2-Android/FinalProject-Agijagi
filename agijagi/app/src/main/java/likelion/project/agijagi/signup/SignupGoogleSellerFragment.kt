package likelion.project.agijagi.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.databinding.FragmentGoogleSignupSellerBinding

class SignupGoogleSellerFragment : Fragment() {

    private var _fragmentGoogleSignupSellerBinding: FragmentGoogleSignupSellerBinding? = null
    private val fragmentGoogleSignupSellerBinding get() = _fragmentGoogleSignupSellerBinding!!

    private var auth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore

    private var NameState = false

    private var buttonState = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _fragmentGoogleSignupSellerBinding =
            FragmentGoogleSignupSellerBinding.inflate(inflater, container, false)

        return fragmentGoogleSignupSellerBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        setup()

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