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

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarItemAction()
        setCategoryMenuButton()
    }

    private fun setToolbarItemAction() {
        binding.toolbarCategory.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_category_shopping_list -> {
                    findNavController().navigate(R.id.action_categoryFragment_to_shoppingListFragment)
                }
            }
            false
        }
    }

    private fun setCategoryMenuButton() {
        binding.run {
            linearlayoutCategoryButtonAll.setOnClickListener {
                it.navigateToCategoryDetail("All", 0)
            }
            linearlayoutCategoryButtonPlate.setOnClickListener {
                it.navigateToCategoryDetail("Plate", -1)
            }
            linearlayoutCategoryButtonCup.setOnClickListener {
                it.navigateToCategoryDetail("Cup", -1)
            }
            linearlayoutCategoryButtonBowl.setOnClickListener {
                it.navigateToCategoryDetail("Bowl", -1)
            }
            linearlayoutCategoryButtonOrdermade.setOnClickListener {
                it.findNavController()
                    .navigate(R.id.action_categoryFragment_to_orderMadeCategoryFragment)
            }
        }
    }

    private fun View.navigateToCategoryDetail(category: String, is_custom: Int) {
        // 0 == all , 1 == is_custom = true, -1 == is_custom = false
        binding.run {
            val bundle = Bundle()
            bundle.putString("category", category)
            bundle.putInt("is_custom", is_custom)
            findNavController().navigate(
                R.id.action_categoryFragment_to_categoryDetailInfoListFragment,
                bundle
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

