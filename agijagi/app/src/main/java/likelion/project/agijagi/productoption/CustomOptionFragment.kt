package likelion.project.agijagi.productoption

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentCustomOptionBinding

class CustomOptionFragment : Fragment() {
    lateinit var customOptionBinding: FragmentCustomOptionBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        customOptionBinding = FragmentCustomOptionBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        customOptionBinding.run {

            menuCustomOptionSelectText.setOnItemClickListener { adapterView, view, i, l ->
                when (i) {
                    0 -> {
                        layoutCustomLetteringOption.isVisible = true
                        layoutCustomPrintOption.isGone = true
                    }
                    1 -> {
                        layoutCustomLetteringOption.isGone = true
                        layoutCustomPrintOption.isVisible = true
                    }
                    2 -> {
                        layoutCustomLetteringOption.isVisible = true
                        layoutCustomPrintOption.isVisible = true
                    }
                }
            }
        }

        return customOptionBinding.root
    }

}