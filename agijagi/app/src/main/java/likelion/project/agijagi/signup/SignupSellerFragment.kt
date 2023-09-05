package likelion.project.agijagi.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentSignupBuyerBinding
import likelion.project.agijagi.databinding.FragmentSignupSellerBinding

class SignupSellerFragment : Fragment() {

    private var _fragmentSignupSellerBinding: FragmentSignupSellerBinding? = null
    private val fragmentSignupSellerBinding get() = _fragmentSignupSellerBinding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _fragmentSignupSellerBinding = FragmentSignupSellerBinding.inflate(inflater, container, false)

        return fragmentSignupSellerBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentSignupSellerBinding.run {
            toolbarSignupSellerToolbar.setNavigationOnClickListener {
                findNavController().navigate(R.id.action_signupSellerFragment_to_signupSelectFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentSignupSellerBinding = null
    }
}