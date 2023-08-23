package likelion.project.agijagi.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import likelion.project.agijagi.databinding.FragmentProductDetailBinding


class ProductDetailFragment : Fragment() {

    private var binding: FragmentProductDetailBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductDetailBinding.inflate(inflater)

        clickFavoriteButton()

        return binding?.root
    }

    private fun clickFavoriteButton() {
        binding?.run {
            imageButtonProductDetailFavorite.run {
                setOnClickListener {
                    isSelected = isSelected != true
                }
            }
        }
    }

}