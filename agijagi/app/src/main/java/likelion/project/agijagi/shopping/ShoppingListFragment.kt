package likelion.project.agijagi.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.databinding.FragmentShoppingListBinding

class ShoppingListFragment : Fragment() {

    lateinit var shoppingListBinding: FragmentShoppingListBinding
    lateinit var mainActivity: MainActivity
    lateinit var shoppingListAdapter: ShoppingListAdapter

    val dataSet = arrayListOf<ShoppingListModel>().apply {
        add(ShoppingListModel("김자기", "화려한 접시", "2억원"))
        add(ShoppingListModel("아기자기", "아름다운 접시", "2원"))
        add(ShoppingListModel("아기자기", "큰접시", "20.000,000원"))
        add(ShoppingListModel("아기자기", "부서진 접시", "20원"))
        add(ShoppingListModel("김자기", "아쉬운 접시", "2,001,402,414원"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        shoppingListBinding = FragmentShoppingListBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        shoppingListAdapter = ShoppingListAdapter()

        shoppingListBinding.run {
            toolbarShoppinglist.run {
                title = "장바구니"
            }

            recyclerviewShoppingList.run {
                layoutManager = LinearLayoutManager(mainActivity)
                adapter = shoppingListAdapter
            }
            shoppingListAdapter.submitList(dataSet)
        }
        return shoppingListBinding.root
    }
}