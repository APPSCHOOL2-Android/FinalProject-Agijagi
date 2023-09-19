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
            linearlayoutCategoryButtonAll.setOnClickListener { it.navigateToCategoryDetail("All") }
            linearlayoutCategoryButtonPlate.setOnClickListener { it.navigateToCategoryDetail("Plate") }
            linearlayoutCategoryButtonCup.setOnClickListener { it.navigateToCategoryDetail("Cup") }
            linearlayoutCategoryButtonBowl.setOnClickListener { it.navigateToCategoryDetail("Bowl") }
            linearlayoutCategoryButtonOrdermade.setOnClickListener {
                it.findNavController()
                    .navigate(R.id.action_categoryFragment_to_orderMadeCategoryFragment)
            }
        }
    }

    private fun View.navigateToCategoryDetail(category: String) {
        binding.run {
            val bundle = Bundle()
            bundle.putString("category", category)
            findNavController().navigate(R.id.action_categoryFragment_to_categoryDetailInfoListFragment,bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

