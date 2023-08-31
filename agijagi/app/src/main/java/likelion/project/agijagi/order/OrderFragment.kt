package likelion.project.agijagi.order

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentOrderBinding

class OrderFragment : Fragment() {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!

    lateinit var orderAdapter: OrderAdapter

    val dataList = arrayListOf<OrderModel>().apply {
        add(OrderModel("배송 중","고려","고려청자","145,000원"))
        add(OrderModel("배송 대기","아기","자기","345,000원"))
        add(OrderModel("배송 완료","자기","아기","645,000원"))
        add(OrderModel("제작 중","라이크","라이언","565,000원"))
        add(OrderModel("배송 중","라이언","라이크","945,000원"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderAdapter = OrderAdapter()

        binding.run {
            recyclerviewOrder.run {
                adapter = orderAdapter
                layoutManager = LinearLayoutManager(context)
            }
            orderAdapter.submitList(dataList)
        }
    }


}