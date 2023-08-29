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

    private lateinit var fragmentHomeBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater)

        toolbarMenuItemScreenNavigate()
        orderMadeProductScreenNavigate()

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
}