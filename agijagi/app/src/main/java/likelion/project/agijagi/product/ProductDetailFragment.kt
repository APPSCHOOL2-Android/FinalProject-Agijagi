package likelion.project.agijagi.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentProductDetailBinding

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarMenuItem()
        setFavoriteButton()
        setPurchaseButton()
    }

    private fun setToolbarMenuItem() {
        binding.toolbarProductDetail.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_product_detail_shopping -> {
                    findNavController().navigate(R.id.action_productDetailFragment_to_shoppingListFragment)
                }
            }
            false
        }
    }

    private fun setFavoriteButton() {
        binding.imageButtonProductDetailFavorite.setOnClickListener {
            it.isSelected = it.isSelected != true
        }
    }

    private fun setPurchaseButton() {
        binding.buttonProductDetailPurchase.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_productDetailFragment_to_readyMadeOptionFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}