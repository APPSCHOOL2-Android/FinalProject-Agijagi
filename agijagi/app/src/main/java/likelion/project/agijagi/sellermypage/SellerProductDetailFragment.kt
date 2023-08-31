package likelion.project.agijagi.sellermypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentSellerProductDetailBinding

class SellerProductDetailFragment : Fragment() {

    private var binding: FragmentSellerProductDetailBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSellerProductDetailBinding.inflate(inflater)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbarClickItem()
    }
    private fun toolbarClickItem() {
        binding?.run {
            toolbarSellerProductDetail.run {
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_product_detail_edit -> {
                            findNavController().navigate(R.id.action_sellerProductDetailFragment_to_productUpdateFragment)
                        }

                        R.id.menu_product_detail_delete -> {

                        }
                    }
                    false
                }
            }
        }
    }

}