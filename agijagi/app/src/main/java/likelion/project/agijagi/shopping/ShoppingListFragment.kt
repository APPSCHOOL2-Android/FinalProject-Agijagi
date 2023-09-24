package likelion.project.agijagi.shopping

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentShoppingListBinding
import likelion.project.agijagi.model.ProdInfo
import likelion.project.agijagi.model.UserModel

class ShoppingListFragment : Fragment() {

    private var _binding: FragmentShoppingListBinding? = null
    private val binding get() = _binding!!
    lateinit var mainActivity: MainActivity
    lateinit var shoppingListAdapter: ShoppingListAdapter

    private val dataSet = arrayListOf<ShoppingListModel>()
    private val shoppingList = hashMapOf<String, String>()
    private val countList = hashMapOf<String, String>()

    private val db = Firebase.firestore
    private val storageRef = Firebase.storage.reference

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
            runBlocking {
                getShoppingListData()
            }

            toolbarShoppinglist.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            // 체크 초기화
            shoppingListAdapter.setCheckBoxParentState { setCheckBoxParentState() }

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
            buttonDeleteSelected.setOnClickListener {
                // 선택된 메뉴 지우기
                removeData()
                shoppingListAdapter.notifyDataSetChanged()
            }

            buttonShoppingListOk.setOnClickListener {
                // isChecked 되어있는거 prodId paymentFragment에 보내주면된다.
                val data = dataSet.filter { it.isCheck }
                Log.d("ShoppingListFragment.onViewCreated()", data[0].documentId)
                db.collection("buyer").document(UserModel.roleId)
                    .collection("shopping_list").document(data[0].documentId).get()
                    .addOnSuccessListener {
                        val customOption = ProdInfo(
                            it.getBoolean("isCustom")!!,
                            it["prodInfoId"].toString(),
                            it["count"].toString().toLong(),
                            it.get("diagram") as HashMap<String, String?>,
                            it["option"].toString(),
                            it["price"].toString(),
                            it["customWord"].toString(),
                            it["customLocation"].toString(),
                        )
                        val bundle = Bundle().apply {
                            putParcelable("prodInfo", customOption)
                        }
                        findNavController().navigate(
                            R.id.action_shoppingListFragment_to_paymentFragment, bundle
                        )
                    }
            }
        }
    }

    private fun setRecyclerView() {
        binding.run {
            recyclerviewShoppingList.run {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = shoppingListAdapter
            }
            shoppingListAdapter.submitList(dataSet)
        }
        shoppingListAdapter.notifyDataSetChanged()
    }

    private fun showSampleData(isLoading: Boolean) {
        if (isLoading) {
            binding.shimmerShoppingList.startShimmer()
            binding.shimmerShoppingList.visibility = View.VISIBLE
            binding.recyclerviewShoppingList.visibility = View.GONE
        } else {
            binding.shimmerShoppingList.stopShimmer()
            binding.shimmerShoppingList.visibility = View.GONE
            binding.recyclerviewShoppingList.visibility = View.VISIBLE
        }
    }

    private suspend fun getShoppingListData() {
        showSampleData(true)
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("ShoppingListFragment.getShoppingListData()", "쇼핑리스트 시작 ${dataSet.size}")
            db.collection("buyer").document(UserModel.roleId)
                .collection("shopping_list").get()
                .addOnSuccessListener { shopping ->
                    shoppingList.clear()
                    Log.d("ShoppingListFragment.getShoppingListData()", "쇼핑리스트 들어옴 ${dataSet.size}")
                    for (document in shopping) {
                        shoppingList[document.id] = document["prodInfoId"].toString()
                        countList[document.id] = document["count"].toString()
                    }
                }
        }

        CoroutineScope(Dispatchers.IO).launch {
            delay(100)
            dataSet.clear()
            Log.d("ShoppingListFragment.getShoppingListData()", "코루틴 시작 ${dataSet.size}")
            val snapshot = db.collection("product").get().await()
            shoppingList.forEach { productId ->
                for (document in snapshot) {
                    val thumbnailImage = document.data["thumbnail_image"].toString()
                    if (productId.value == document.id) {
                        val uri = storageRef.child(thumbnailImage).downloadUrl.await()
                        Log.d("ShoppingListFragment.getShoppingListData()", "productId ${productId.value}")

                        dataSet.add(
                            ShoppingListModel(
                                document["brand"].toString(),
                                document["name"].toString(),
                                document["price"].toString(),
                                countList[productId.key].toString(),
                                uri.toString(),
                                productId.key
                            )
                        )
                    }
                }
                Log.d("ShoppingListFragment.getShoppingListData()", "코루틴 끝 ${dataSet.size}")
            }
            withContext(Dispatchers.Main) {
                showSampleData(false)
                setRecyclerView()
                Log.d("ShoppingListFragment.getShoppingListData()", "데이터 그릴때 ${dataSet.size}")
            }
        }
    }

    private fun removeData() {
        // 연결 리스트 해제
        val tempList = dataSet.filter { it.isCheck }
        dataSet.removeAll(tempList)

        var ch = true
        for (data in tempList) {
            db.collection("buyer").document(UserModel.roleId)
                .collection("shopping_list")
                .document(data.documentId)
                .delete()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                    } else {
                        ch = false
                    }
                }
        }

        if (!ch) {
            Log.e("FirebaseException", "하나 이상의 통신 실패")
        }

        runBlocking {
            getShoppingListData()
        }
        setCheckBoxParentState()
    }

    private fun setCheckBoxParentState() {
        val checkedCount = dataSet.filter { item -> item.isCheck }.size
        var state = -1
        var str = " 전체 선택"
        when (checkedCount) {
            0 -> {
                state = MaterialCheckBox.STATE_UNCHECKED
                binding.buttonShoppingListOk.setBackgroundResource(R.drawable.wide_box_bottom_inactive)
                binding.buttonShoppingListOk.isClickable = false
                binding.textviewOrderPrice.text = "0원"
                binding.textviewShoppingShipPrice.text = "0원"
                binding.textviewOrderAllPrice.text = "0원"
            }

            1 -> {
                val data = dataSet.filter { item -> item.isCheck }
                val price = (data[0].price.toLong()) * (data[0].count.toLong())
                binding.buttonShoppingListOk.setBackgroundResource(R.drawable.wide_box_bottom_active)
                binding.buttonShoppingListOk.isClickable = true
                binding.textviewOrderPrice.text = "${price}원"
                binding.textviewShoppingShipPrice.text = "3000원"
                binding.textviewOrderAllPrice.text = "${price + 3000}원"
            }

            dataSet.size -> { // ALL
                state = MaterialCheckBox.STATE_CHECKED
                str = " 선택 해제"
                binding.buttonShoppingListOk.setBackgroundResource(R.drawable.wide_box_bottom_inactive)
                binding.buttonShoppingListOk.isClickable = false
                binding.textviewOrderPrice.text = "0원"
                binding.textviewShoppingShipPrice.text = "0원"
                binding.textviewOrderAllPrice.text = "0원"
            }

            else -> {
                state = MaterialCheckBox.STATE_INDETERMINATE
                str = " 전체 ${checkedCount}개"
                binding.buttonShoppingListOk.setBackgroundResource(R.drawable.wide_box_bottom_inactive)
                binding.buttonShoppingListOk.isClickable = false
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