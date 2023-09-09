package likelion.project.agijagi.sellermypage

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentProductListBinding
import likelion.project.agijagi.sellermypage.adapter.ProductListAdapter
import likelion.project.agijagi.sellermypage.model.ProductListModel

class ProductListFragment : Fragment() {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!
    lateinit var productListAdapter: ProductListAdapter

    val dataList = arrayListOf<ProductListModel>().apply {
        add(ProductListModel("품절", "고려", "고려청자", "145,000원", "23.07.07"))
        add(ProductListModel("판매", "아기", "자기", "345,000원", "23.07.17"))
        add(ProductListModel("판매", "자기", "아기", "645,000원", "23.07.18"))
        add(ProductListModel("판매", "라이크", "라이언", "565,000원", "23.08.22"))
        add(ProductListModel("품절", "라이언", "라이크", "945,000원", "23.08.25"))
        add(ProductListModel("판매", "라이언", "라이크", "945,000원", "23.09.07"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productListAdapter = ProductListAdapter(requireContext())

        binding.run {
            setToolbarItemAction()

            recyclerviewProductList.run {
                adapter = productListAdapter
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(MarginItemDecoration(40))
            }
            productListAdapter.submitList(dataList)

            // dataList가 비어있을 때 보이도록
            if (dataList.isEmpty()) {
                textViewProductListEmpty.visibility = View.VISIBLE
            } else {
                textViewProductListEmpty.visibility = View.GONE
            }
        }
    }

    private fun setToolbarItemAction() {
        binding.toolbarProductList.run {
            setNavigationOnClickListener {
                findNavController().navigate(R.id.action_productListFragment_to_sellerMypageFragment)
            }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_product_list_add -> {
                        findNavController().navigate(R.id.action_productListFragment_to_productAddFragment)
                    }
                }
                false
            }
        }
    }


    inner class MarginItemDecoration(private val spaceSize: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val itemCount = state.itemCount

            // px -> dp 변환
            val transformationDP = resources.displayMetrics.density
            val size = (spaceSize * transformationDP).toInt()

            with(outRect) {
                bottom = if (position == itemCount - 1) size else 0
            }
        }
    }
}