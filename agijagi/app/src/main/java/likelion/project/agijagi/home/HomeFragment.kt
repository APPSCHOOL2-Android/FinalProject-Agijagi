package likelion.project.agijagi.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    lateinit var fragmentHomeBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater)

        toolbarMenuItemScreenNavigate()
        orderMadeProductScreenNavigate()
        setBestItemsListener()

        return fragmentHomeBinding.root

    }

    private fun toolbarMenuItemScreenNavigate() {
        fragmentHomeBinding.toolbarHome.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_home_search -> {
                    findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
                }

                R.id.menu_home_shopping_list -> {
                    findNavController().navigate(R.id.action_homeFragment_to_shoppingListFragment)
                }
            }
            false
        }
    }

    private fun orderMadeProductScreenNavigate() {
        fragmentHomeBinding.linearlayoutHomeToOrderMadeCategory.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_orderMadeCategoryFragment)
        }
    }

    private fun setBestItemsListener() {
        fragmentHomeBinding.run {
            linearlayoutHomeButtonOne.setOnClickListener { it.navigateToProductDetail() }
            linearlayoutHomeButtonTwo.setOnClickListener { it.navigateToProductDetail() }
            linearlayoutHomeButtonThree.setOnClickListener { it.navigateToProductDetail() }
            linearlayoutHomeButtonFour.setOnClickListener { it.navigateToProductDetail() }
        }
    }

    private fun View.navigateToProductDetail() {
        fragmentHomeBinding.run {
            // 추후 상품 종류(기성품, 주문제작)에 맞게 화면 전환할 수 있도록 수정
            findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment)
        }
    }
}