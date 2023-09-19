package likelion.project.agijagi.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.category.adapter.CategoryDetailInfoListAdapter
import likelion.project.agijagi.category.model.CategoryDetailInfoListModel
import likelion.project.agijagi.databinding.FragmentCategoryDetailInfoListBinding

class CategoryDetailInfoListFragment : Fragment() {

    lateinit var fragmentCategoryDetailInfoListBinding: FragmentCategoryDetailInfoListBinding
    lateinit var mainActivity: MainActivity
    lateinit var listAdapter: CategoryDetailInfoListAdapter

    private var auth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore

    var getBundle = ""
    val dataList = mutableListOf<CategoryDetailInfoListModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentCategoryDetailInfoListBinding = FragmentCategoryDetailInfoListBinding.inflate(inflater)

        return fragmentCategoryDetailInfoListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarMenuItem()

        listAdapter = CategoryDetailInfoListAdapter()

        fragmentCategoryDetailInfoListBinding.run {
            toolbarCategoryDetailInfoList.run {

                // category 클릭 시 정보를 전달 받아서 title 변경 필요
                getBundle = arguments?.getString("category").toString()
                title = getBundle
                inflateMenu(R.menu.menu_category)
            }

            recyclerviewCategoryDetailInfoList.run {
                layoutManager = GridLayoutManager(context, 2)
                adapter = listAdapter
            }
        }
    }

    private fun getCategoryList(category: String){
        // is_custom 을 통해 order made인지 아닌지 분류
        if(category == "All"){
            db.collection("product")
        } else {

        }
    }

    private fun setToolbarMenuItem() {
        fragmentCategoryDetailInfoListBinding.toolbarCategoryDetailInfoList.run {
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_category_shopping_list -> {
                        findNavController().navigate(R.id.action_categoryDetailInfoListFragment_to_shoppingListFragment)
                    }
                }
                false
            }
        }
    }

    fun setup() {
        db = Firebase.firestore

        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
    }
}