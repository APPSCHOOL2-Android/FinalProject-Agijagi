package likelion.project.agijagi.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentCustomProductDetailBinding

class CustomProductDetailFragment : Fragment() {

    private var binding: FragmentCustomProductDetailBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCustomProductDetailBinding.inflate(inflater)

        toolbarClickItem()
        clickFloatingButton()
        clickFloorPlanDownloadButton()
        clickFavoriteButton()
        clickPurchaseButtonToCustomOption()

        return binding?.root
    }

    private fun toolbarClickItem() {
        binding?.run {
            toolbarCustomProductDetail.run {
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_product_detail_shopping -> {
                            findNavController().navigate(R.id.action_customProductDetailFragment_to_shoppingListFragment)
                        }
                    }
                    false
                }
            }
        }
    }

    private fun clickFloatingButton() {
        binding?.run {
            customFloatingButtonCustomProductDetailToChatting.customFloatingButtonLayout.setOnClickListener {
                it.findNavController().navigate(R.id.action_customProductDetailFragment_to_userChatListFragment)
            }
        }
    }

    private fun clickFloorPlanDownloadButton() {
        binding?.run {
            buttonCustomProductDetailDownloadFloorPlan.setOnClickListener {
                Snackbar.make(it, "도면 다운로드가 완료되었습니다.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun clickFavoriteButton() {
        binding?.run {
            imageButtonCustomProductDetailFavorite.run {
                setOnClickListener {
                    isSelected = isSelected != true
                }
            }
        }
    }

    private fun clickPurchaseButtonToCustomOption() {
        binding?.run {
            buttonCustomProductDetailPurchase.setOnClickListener {
                it.findNavController()
                    .navigate(R.id.action_customProductDetailFragment_to_customOptionFragment)
            }
        }
    }

}