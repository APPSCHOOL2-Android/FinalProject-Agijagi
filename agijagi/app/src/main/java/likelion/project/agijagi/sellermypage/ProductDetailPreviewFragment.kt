package likelion.project.agijagi.sellermypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import likelion.project.agijagi.R
import likelion.project.agijagi.UserEssential.Companion.db
import likelion.project.agijagi.UserEssential.Companion.getMillisec
import likelion.project.agijagi.databinding.FragmentProductDetailPreviewBinding
import likelion.project.agijagi.model.ProductModel
import java.text.DecimalFormat

class ProductDetailPreviewFragment : Fragment() {

    private var _binding: FragmentProductDetailPreviewBinding? = null
    private val binding get() = _binding!!

    lateinit var product: ProductModel
    val dec = DecimalFormat("#,###")

    val customOptionInfo = hashMapOf(
        "image_fee" to 5000,
        "image_is_use" to true,
        "lettering_fee" to 0,
        "lettering_is_use" to false
    )

    val productMap = hashMapOf(
        "brand" to "브랜드명2",
        "category" to "카테고리명2",
        "customOptionInfo" to customOptionInfo,
        "detail" to "상세내용상세내용상세내용상세내용상세내용상세내용상세내용",
        "floor_plan" to arrayListOf(
            "floor_plan/aaaa",
            "floor_plan/bbbb",
            "floor_plan/cccc",
            "floor_plan/dddd"
        ),
        "image" to arrayListOf(
            "image/eeee",
            "image/ffff",
            "image/gggg",
            "image/hhhh",
            "image/iiii",
            "image/jjjj"
        ),
        "is_custom" to true,
        "name" to "상품명2",
        "out_of_stock" to false,
        "price" to 40000,
        "sales_quantity" to 60,
        "seller_id" to "dsd45s6dfw55543fe",
        "update_date" to getMillisec()
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailPreviewBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getProductDataFromArguments()
        initViews()
        setToolbarNavigationAction()
        setupProductRegistrationButton()
    }

    private fun getProductDataFromArguments() {
        val bundle = arguments

        if (bundle != null) {
            product = bundle.getParcelable("productData")!!
        }
    }

    private fun initViews() {
        binding.run {
//            Glide.with(this@ProductDetailPreviewFragment).run {
//                load(product.image[0]).into(imageviewProductDetailPreviewImage1)
//                load(product.image[1]).into(imageviewProductPreviewDetailImage2)
//                load(product.image[2]).into(imageviewProductPreviewDetailImage3)
//                load(product.image[3]).into(imageviewProductPreviewDetailImage4)
//                load(product.image[4]).into(imageviewProductPreviewDetailImage5)
//                load(product.image[5]).into(imageviewProductPreviewDetailImage6)
//                // 이미지 하나 더 추가
//            }
            textviewProductDetailPreviewBrand.text = product.brand
            textviewProductDetailPreviewName.text = product.name
            "${dec.format(product.price.toInt())}원".also {
                textviewProductDetailPreviewPrice.text = it
            }
            textviewProductDetailPreviewInfoTitle.text = product.name
            textviewProductDetailPreviewInfoDescription.text = product.detail
        }
    }

    private fun registerProductData() {
        db = FirebaseFirestore.getInstance()
        db.collection("product").document(getMillisec()).set(productMap)
    }

    private fun setToolbarNavigationAction() {
        binding.toolbarProductDetailPreview.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupProductRegistrationButton() {
        binding.buttonProductDetailPreviewProductRegistration.run {
            text = "등록"
            setOnClickListener {
                registerProductData()
                Snackbar.make(it, "상품 등록이 완료되었습니다.", Snackbar.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_productDetailPreviewFragment_to_productListFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}