package likelion.project.agijagi.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentCategoryBinding

class CategoryFragment : Fragment() {

    lateinit var fragmentCategoryBinding: FragmentCategoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentCategoryBinding = FragmentCategoryBinding.inflate(inflater)

        toolbarMenuItemScreenNavigate()
        setCategoryMenuListener()

        return fragmentCategoryBinding.root
    }

    private fun toolbarMenuItemScreenNavigate() {
        fragmentCategoryBinding.toolbarCategory.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_category_shopping_list -> {
                    findNavController().navigate(R.id.action_categoryFragment_to_shoppingListFragment)
                }
            }
            false
        }
    }

    private fun setCategoryMenuListener() {
        fragmentCategoryBinding.run {
            linearlayoutCategoryButtonOne.setOnClickListener { it.navigateToCategoryDetail() }
            linearlayoutCategoryButtonTwo.setOnClickListener { it.navigateToCategoryDetail() }
            linearlayoutCategoryButtonThree.setOnClickListener { it.navigateToCategoryDetail() }
            linearlayoutCategoryButtonFour.setOnClickListener { it.navigateToCategoryDetail() }
            linearlayoutCategoryButtonFive.setOnClickListener {
                it.findNavController()
                    .navigate(R.id.action_categoryFragment_to_orderMadeCategoryFragment)
            }
        }
    }

    private fun View.navigateToCategoryDetail() {
        fragmentCategoryBinding.run {
            findNavController().navigate(R.id.action_categoryFragment_to_categoryDetailFragment)
        }
    }
}

