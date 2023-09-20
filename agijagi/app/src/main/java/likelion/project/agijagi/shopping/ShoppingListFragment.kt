package likelion.project.agijagi.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.checkbox.MaterialCheckBox
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.databinding.FragmentShoppingListBinding

class ShoppingListFragment : Fragment() {

    private var _binding: FragmentShoppingListBinding? = null
    private val binding get() = _binding!!
    lateinit var mainActivity: MainActivity
    lateinit var shoppingListAdapter: ShoppingListAdapter

    private val dataSet = arrayListOf<ShoppingListModel>()
        .apply {
        add(ShoppingListModel("김자기", "화려한 접시", "2억원"))
        add(ShoppingListModel("아기자기", "아름다운 접시", "2원"))
        add(ShoppingListModel("아기자기", "큰접시", "20.000,000원"))
        add(ShoppingListModel("아기자기", "부서진 접시", "20원"))
        add(ShoppingListModel("김자기", "아쉬운 접시", "2,001,402,414원"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentShoppingListBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        shoppingListAdapter = ShoppingListAdapter(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.run {
            toolbarShoppinglist.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            //
            shoppingListAdapter.setCheckBoxParentState { setCheckBoxParentState() }

            recyclerviewShoppingList.run {
                layoutManager = LinearLayoutManager(mainActivity)
                adapter = shoppingListAdapter
            }
            shoppingListAdapter.submitList(dataSet)

            // 체크 초기화
            if (dataSet.size == 0) {
                setCheckBoxParentState()
            } else {
                for (data in dataSet) {
                    data.isCheck = false
                }
            }

            // 전체 선택 버튼
            checkboxShoppingListAllItem.setOnCheckedChangeListener { compoundButton, b ->
                when (checkboxShoppingListAllItem.checkedState) {
                    MaterialCheckBox.STATE_CHECKED -> {
                        dataSet.forEach { it.isCheck = true }
                    }

                    MaterialCheckBox.STATE_UNCHECKED -> {
                        dataSet.forEach { it.isCheck = false }
                    }
                }
                if (!recyclerviewShoppingList.isComputingLayout) {
                    shoppingListAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun setCheckBoxParentState() {
        val checkedCount = dataSet.filter { item -> item.isCheck }.size
        var state = -1
        var str = " 전체 선택"
        when (checkedCount) {
            0 -> {
                state = MaterialCheckBox.STATE_UNCHECKED
            }

            dataSet.size -> { // ALL
                state = MaterialCheckBox.STATE_CHECKED
                str = " 선택 해제"
            }

            else -> {
                state = MaterialCheckBox.STATE_INDETERMINATE
                str = " 전체 ${checkedCount}개"
            }
        }

        binding.checkboxShoppingListAllItem.checkedState = state
        binding.checkboxShoppingListAllItem.text = str
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}