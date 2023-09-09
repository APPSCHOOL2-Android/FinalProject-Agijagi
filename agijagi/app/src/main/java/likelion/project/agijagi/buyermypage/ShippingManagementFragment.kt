package likelion.project.agijagi.buyermypage

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
import likelion.project.agijagi.buyermypage.adapter.ShippingManagementAdapter
import likelion.project.agijagi.buyermypage.model.ShippingManagementModel
import likelion.project.agijagi.databinding.FragmentShippingManagementBinding

class ShippingManagementFragment : Fragment() {

    private var _binding: FragmentShippingManagementBinding? = null
    private val binding get() = _binding!!
    lateinit var shippingManagementAdapter: ShippingManagementAdapter

    val dataList = arrayListOf<ShippingManagementModel>().apply {
        add(ShippingManagementModel("집", "010-1111-2222", "서울시 마포구"))
        add(ShippingManagementModel("회사", "1577-1577", "경기도 고양시"))
        add(ShippingManagementModel("어느곳", "070-1577-1577", "경기도 용인시"))
        add(ShippingManagementModel("다른수령지", "010-4684-1577", "서울"))
        add(ShippingManagementModel("완전 다른수령지", "010-6342-8674", "서울시 어디구"))
        add(ShippingManagementModel("또 다른수령지", "010-8549-1577", "서울"))
        add(ShippingManagementModel("그리고 완전 다른수령지", "010-2454-8674", "서울시 어디구"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShippingManagementBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarItemAction()
        setShippingAddButton()

        shippingManagementAdapter = ShippingManagementAdapter()

        binding.run {

            recyclerviewShippingManagement.run {
                adapter = shippingManagementAdapter
                layoutManager = LinearLayoutManager(context)

                addItemDecoration(MarginItemDecoration(40))
            }

            // dataList가 비어있을 때 보이도록
            if (dataList.isEmpty()) {
                textViewShippingManagemnetNull.visibility = View.VISIBLE
            } else {
                textViewShippingManagemnetNull.visibility = View.GONE
            }

            shippingManagementAdapter.submitList(dataList)
        }
    }

    private fun setToolbarItemAction() {
        binding.toolbarShippingManagement.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setShippingAddButton() {
        binding.buttonShippingManagementAdd.setOnClickListener {
            findNavController().navigate(R.id.action_shippingManagementFragment_to_shippingAddFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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