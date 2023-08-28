package likelion.project.agijagi.sellermypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.databinding.FragmentProductAddBinding

class ProductAddFragment : Fragment() {

    lateinit var fragmentProductAddBinding: FragmentProductAddBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentProductAddBinding = FragmentProductAddBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentProductAddBinding.run {

        }

        return fragmentProductAddBinding.root
    }

}
