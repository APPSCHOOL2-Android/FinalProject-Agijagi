package likelion.project.agijagi.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import likelion.project.agijagi.databinding.FragmentCustomProductDetailBinding

class CustomProductDetailFragment : Fragment() {

    private var binding: FragmentCustomProductDetailBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCustomProductDetailBinding.inflate(inflater)

        clickFloatingButton()

        return binding?.root
    }

    private fun clickFloatingButton() {
        binding?.run {
            customFloatingButtonCustomProductDetail.customFloatingButtonLayout.setOnClickListener {
                Toast.makeText(context, "Custom Floating Button Clicked!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

}