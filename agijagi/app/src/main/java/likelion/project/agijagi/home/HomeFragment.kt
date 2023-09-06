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

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarMenuItem()
        setOrderMadeProductButton()
        setBestItemsButton()
    }

    private fun setToolbarMenuItem() {
        binding.toolbarHome.setOnMenuItemClickListener {
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

    private fun setOrderMadeProductButton() {
        binding.linearlayoutHomeToOrderMadeCategory.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_orderMadeCategoryFragment)
        }
    }

    private fun setBestItemsButton() {
        binding.run {
            linearlayoutHomeButtonOne.setOnClickListener { it.navigateToProductDetail() }
            linearlayoutHomeButtonTwo.setOnClickListener { it.navigateToProductDetail() }
            linearlayoutHomeButtonThree.setOnClickListener { it.navigateToProductDetail() }
            linearlayoutHomeButtonFour.setOnClickListener { it.navigateToProductDetail() }
        }
    }

    private fun View.navigateToProductDetail() {
        binding.run {
            // 추후 상품 종류(기성품, 주문제작)에 맞게 화면 전환할 수 있도록 수정
            findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}