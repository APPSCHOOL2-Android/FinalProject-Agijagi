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

    private var _binding: FragmentOrderMadeCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderMadeCategoryBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarListener()
        setOrderMadeCategoryMenuButton()
    }

    private fun setToolbarListener() {
        binding.toolbarCategory.run {
            setNavigationOnClickListener {
                findNavController().popBackStack()
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

    private fun setOrderMadeCategoryMenuButton() {
        binding.run {
            linearlayoutOrderMadeCategoryButtonOne.setOnClickListener { it.navigateToCategoryDetail() }
            linearlayoutOrderMadeCategoryButtonTwo.setOnClickListener { it.navigateToCategoryDetail() }
            linearlayoutOrderMadeCategoryButtonThree.setOnClickListener { it.navigateToCategoryDetail() }
            linearlayoutOrderMadeCategoryButtonFour.setOnClickListener { it.navigateToCategoryDetail() }
        }
    }

    private fun View.navigateToCategoryDetail() {
        binding.run {
            findNavController().navigate(R.id.action_orderMadeCategoryFragment_to_categoryDetailInfoListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}