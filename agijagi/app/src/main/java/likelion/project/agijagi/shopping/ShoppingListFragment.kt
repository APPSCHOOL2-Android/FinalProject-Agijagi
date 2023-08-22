package likelion.project.agijagi.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentShoppingListBinding

class ShoppingListFragment : Fragment() {
    lateinit var fragmentShoppingListBinding: FragmentShoppingListBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentShoppingListBinding = FragmentShoppingListBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        return fragmentShoppingListBinding.root
    }
}