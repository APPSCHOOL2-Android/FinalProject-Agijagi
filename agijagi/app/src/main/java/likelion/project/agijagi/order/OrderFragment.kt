package likelion.project.agijagi.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentOrderBinding

class OrderFragment : Fragment() {

    lateinit var fragmentOrderBinding: FragmentOrderBinding
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
        fragmentOrderBinding = FragmentOrderBinding.inflate(inflater)
        orderAdapter = OrderAdapter()

        fragmentOrderBinding.run {
            recyclerviewOrder.run {
                adapter = orderAdapter
                layoutManager = LinearLayoutManager(context)
            }
            orderAdapter.submitList(dataList)
        }

        return fragmentOrderBinding.root
    }

}