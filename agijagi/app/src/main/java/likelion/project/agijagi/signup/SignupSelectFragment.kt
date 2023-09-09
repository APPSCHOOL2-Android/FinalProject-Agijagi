package likelion.project.agijagi.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
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

        fragmentSignupSelectBinding.run {

            // toolbar back button
            toolbarSignupSelectSiginup.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            layoutSignupSelectSeller.setOnClickListener {
                layoutSignupSelectSeller.isSelected = true
                if(layoutSignupSelectSeller.isSelected == true) {
                    buttonSignupSelectSeller.setImageResource(R.drawable.outline_storefront_24_selected)
                }

                findNavController().navigate(R.id.action_signupSelectFragment_to_signupSellerFragment)
            }

            layoutSignupSelectBuyer.setOnClickListener {
                layoutSignupSelectBuyer.isSelected = true
                if(layoutSignupSelectBuyer.isSelected == true) {
                    buttonSignupSelectBuyer.setImageResource(R.drawable.outline_person_outline_24_selected)
                }

                findNavController().navigate(R.id.action_signupSelectFragment_to_signupBuyerFragment)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentSignupSelectBinding = null
    }
}