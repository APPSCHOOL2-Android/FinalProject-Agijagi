package likelion.project.agijagi.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentCustomProductDetailBinding

class CustomProductDetailFragment : Fragment() {

    private var _binding: FragmentCustomProductDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomProductDetailBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarMenuItem()
        setFloatingButton()
        setFloorPlanDownloadButton()
        setFavoriteButton()
        setPurchaseButton()
    }

    private fun setToolbarMenuItem() {
        binding.toolbarCustomProductDetail.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_product_detail_shopping -> {
                    findNavController().navigate(R.id.action_customProductDetailFragment_to_shoppingListFragment)
                }
            }
            false
        }
    }

    private fun setFloatingButton() {
        binding.customFloatingButtonCustomProductDetailToChatting.customFloatingButtonLayout.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_customProductDetailFragment_to_chattingListFragment)
        }
    }

    private fun setFloorPlanDownloadButton() {
        binding.buttonCustomProductDetailDownloadFloorPlan.setOnClickListener {
            Snackbar.make(it, "도면 다운로드가 완료되었습니다.", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setFavoriteButton() {
        binding.imageButtonCustomProductDetailFavorite.setOnClickListener {
            it.isSelected = it.isSelected != true
        }
    }

    private fun setPurchaseButton() {
        binding.buttonCustomProductDetailPurchase.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_customProductDetailFragment_to_customOptionFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}