package likelion.project.agijagi.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentSignupSelectBinding

class SignupSelectFragment : Fragment() {

    private var _binding: FragmentSignupSelectBinding? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser

        binding.run {
            // toolbar back button
            toolbarSignupSelectSiginup.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            layoutSignupSelectBuyer.setOnClickListener {
                layoutSignupSelectBuyer.isSelected = true
                if (layoutSignupSelectBuyer.isSelected)
                    buttonSignupSelectBuyer.setImageResource(R.drawable.outline_person_outline_24_selected)

                if (user != null) {
                    findNavController().navigate(R.id.action_signupSelectFragment_to_signupGoogleBuyerFragment)

                } else {
                    findNavController().navigate(R.id.action_signupSelectFragment_to_signupBuyerFragment)
                }
            }

            layoutSignupSelectSeller.setOnClickListener {
                layoutSignupSelectSeller.isSelected = true
                if (layoutSignupSelectSeller.isSelected)
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
        _binding = null
    }
}