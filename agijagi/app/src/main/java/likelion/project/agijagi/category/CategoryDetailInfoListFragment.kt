package likelion.project.agijagi.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentCategoryDetailInfoListBinding
import likelion.project.agijagi.databinding.FragmentWishListBinding
import likelion.project.agijagi.wishlist.WishListAdapter
import likelion.project.agijagi.wishlist.WishListModel


class CategoryDetailInfoListFragment : Fragment() {
    lateinit var fragmentCategoryDetailInfoListBinding: FragmentCategoryDetailInfoListBinding
    lateinit var mainActivity: MainActivity
    lateinit var listAdapter: CategoryDetailInfoListAdapter

    val dataSet = arrayListOf<CategoryDetailInfoListModel>().apply {
        add(CategoryDetailInfoListModel("김자기", "화려한 접시", "2억원"))
        add(CategoryDetailInfoListModel("아기자기", "아름다운 접시", "2원"))
        add(CategoryDetailInfoListModel("아기자기", "큰접시", "20.000,000원"))
        add(CategoryDetailInfoListModel("아기자기", "부서진 접시", "20원"))
        add(CategoryDetailInfoListModel("김자기", "아쉬운 접시", "2,001,402,414원"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentCategoryDetailInfoListBinding = FragmentCategoryDetailInfoListBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        listAdapter = CategoryDetailInfoListAdapter()

        fragmentCategoryDetailInfoListBinding.run {
            toolbarCategoryDetailInfoList.run {

                // category 클릭 시 정보를 전달 받아서 title 변경 필요
                title = ""
                inflateMenu(R.menu.menu_categoryfragment)
            }

            recyclerviewCategoryDetailInfoList.run {
                layoutManager = GridLayoutManager(context, 2)
                adapter = listAdapter
            }
            listAdapter.submitList(dataSet)
        }
        return fragmentCategoryDetailInfoListBinding.root
    }
}