package likelion.project.agijagi.signup.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentSignupSelectBinding


class SignupSelectFragment : Fragment() {

    private var _fragmentSignupSelectBinding: FragmentSignupSelectBinding? = null
    protected val fragmentSignupSelectBinding
        get() = _fragmentSignupSelectBinding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _fragmentSignupSelectBinding = FragmentSignupSelectBinding.inflate(inflater, container, false)
        return fragmentSignupSelectBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser

        fragmentSignupSelectBinding.run {

            // toolbar back button
            toolbarSignupSelectSiginup.setNavigationOnClickListener {
                view.findNavController().navigate(R.id.action_signupSelectFragment_to_loginFragment)
            }

            layoutSignupSelectBuyer.setOnClickListener {
                layoutSignupSelectBuyer.isSelected = true
                if(layoutSignupSelectBuyer.isSelected == true)
                    buttonSignupSelectBuyer.setImageResource(R.drawable.outline_person_outline_24_selected)

                if (user != null) {
                    findNavController().navigate(R.id.action_signupSelectFragment_to_signupGoogleBuyerFragment)

                } else {
                    findNavController().navigate(R.id.action_signupSelectFragment_to_signupBuyerFragment)
                }
            }

            layoutSignupSelectSeller.setOnClickListener {
                layoutSignupSelectSeller.isSelected = true
                if (layoutSignupSelectSeller.isSelected == true)
                    buttonSignupSelectSeller.setImageResource(R.drawable.outline_storefront_24_selected)

                if (user != null) {
                    findNavController().navigate(R.id.action_signupSelectFragment_to_signupGoogleSellerFragment)
                } else {
                    findNavController().navigate(R.id.action_signupSelectFragment_to_signupSellerFragment)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentSignupSelectBinding = null
    }
}