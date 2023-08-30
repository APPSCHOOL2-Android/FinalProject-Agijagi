package likelion.project.agijagi.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
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
        var view = FragmentSignupSelectBinding.inflate(inflater, container, false)
        return view.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentSignupSelectBinding.run {

            // toolbar back button
            toolbarSignupSelectSiginup.setNavigationOnClickListener {
//                view.findNavController().navigate(R.id.home)
            }

        }
    }
}