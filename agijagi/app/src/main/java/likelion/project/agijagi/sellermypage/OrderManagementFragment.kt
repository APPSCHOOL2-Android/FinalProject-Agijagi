package likelion.project.agijagi.sellermypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.setFragmentResult
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentOrderManagementBinding
import likelion.project.agijagi.sellermypage.adapter.OrderManagementAdapter
import likelion.project.agijagi.sellermypage.model.OrderManagementModel
import likelion.project.agijagi.shopping.ShoppingListModel

class OrderManagementFragment : Fragment() {
    private var _binding: FragmentOrderManagementBinding? = null
    private val binding get() = _binding!!
    lateinit var orderManagementAdapter: OrderManagementAdapter

    val dataSet = arrayListOf<OrderManagementModel>().apply {
        add(OrderManagementModel("김자기", "화려한 접시", "2억원"))
        add(OrderManagementModel("아기자기", "아름다운 접시", "2원"))
        add(OrderManagementModel("아기자기", "큰접시", "20.000,000원"))
        add(OrderManagementModel("아기자기", "부서진 접시", "20원"))
        add(OrderManagementModel("김자기", "아쉬운 접시", "2,001,402,414원"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderManagementBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderManagementAdapter = OrderManagementAdapter(itemClick = { item ->
            val bundle = Bundle().apply {
                putParcelable("orderManagementDetail", item)
            }
            view.findNavController().navigate(
                R.id.action_orderManagementFragment_to_orderManagementDetailFragment,
                bundle
            )
        })
        binding.run {
            recyclerviewOrderManagement.run {
                layoutManager = LinearLayoutManager(context)
                adapter = orderManagementAdapter
            }
            orderManagementAdapter.submitList(dataSet)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}