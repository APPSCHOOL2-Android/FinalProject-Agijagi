package likelion.project.agijagi.wishlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentShoppingListBinding
import likelion.project.agijagi.databinding.FragmentWishListBinding

class WishListFragment : Fragment() {
    lateinit var fragmentWishListBinding: FragmentWishListBinding
    lateinit var mainActivity: MainActivity
    lateinit var listAdapter: WishListAdapter

    val dataSet = arrayListOf<WishListModel>().apply {
        add(WishListModel("김자기", "화려한 접시", "2억원"))
        add(WishListModel("아기자기", "아름다운 접시", "2원"))
        add(WishListModel("아기자기", "큰접시", "20.000,000원"))
        add(WishListModel("아기자기", "부서진 접시", "20원"))
        add(WishListModel("김자기", "아쉬운 접시", "2,001,402,414원"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentWishListBinding = FragmentWishListBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        listAdapter = WishListAdapter()

        fragmentWishListBinding.run {

            toolbarWishList.run {
                title = "찜"
                inflateMenu(R.menu.menu_wish_list)
            }

            wishListRecyclerview.run {
                layoutManager = GridLayoutManager(context, 2)
                adapter = listAdapter
            }
            listAdapter.submitList(dataSet)
        }

        return fragmentWishListBinding.root
    }
}