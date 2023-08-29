package likelion.project.agijagi.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentOrderMadeCategoryBinding

class OrderMadeCategoryFragment : Fragment() {

    lateinit var fragmentOrderMadeCategoryBinding: FragmentOrderMadeCategoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentOrderMadeCategoryBinding = FragmentOrderMadeCategoryBinding.inflate(inflater)

        toolbarItemScreenNavigate()
        setOrderMadeCategoryMenuListener()

        return fragmentOrderMadeCategoryBinding.root
    }

    private fun toolbarItemScreenNavigate() {
        fragmentOrderMadeCategoryBinding.toolbarCategory.run {
            setNavigationOnClickListener {
                it.findNavController()
                    .navigate(R.id.action_orderMadeCategoryFragment_to_categoryFragment)
            }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_category_shopping_list -> {
                        findNavController().navigate(R.id.action_orderMadeCategoryFragment_to_shoppingListFragment)
                    }
                }
                false
            }
        }
    }

    private fun setOrderMadeCategoryMenuListener() {
        fragmentOrderMadeCategoryBinding.run {
            linearlayoutOrderMadeCategoryButtonOne.setOnClickListener { it.navigateToCategoryDetail() }
            linearlayoutOrderMadeCategoryButtonTwo.setOnClickListener { it.navigateToCategoryDetail() }
            linearlayoutOrderMadeCategoryButtonThree.setOnClickListener { it.navigateToCategoryDetail() }
            linearlayoutOrderMadeCategoryButtonFour.setOnClickListener { it.navigateToCategoryDetail() }
        }
    }

    private fun View.navigateToCategoryDetail() {
        fragmentOrderMadeCategoryBinding.run {
            findNavController().navigate(R.id.action_orderMadeCategoryFragment_to_categoryDetailFragment)
        }
    }

}