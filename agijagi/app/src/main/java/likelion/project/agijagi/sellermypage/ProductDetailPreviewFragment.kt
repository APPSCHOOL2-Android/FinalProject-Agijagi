package likelion.project.agijagi.sellermypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.R
import likelion.project.agijagi.UserEssential.Companion.getMillisec
import likelion.project.agijagi.databinding.FragmentProductDetailPreviewBinding
import likelion.project.agijagi.model.ProductModel
import java.text.DecimalFormat

class ProductDetailPreviewFragment : Fragment() {

    private var _binding: FragmentProductDetailPreviewBinding? = null
    private val binding get() = _binding!!

    lateinit var product: ProductModel
    val dec = DecimalFormat("#,###")
    val db = Firebase.firestore

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
        val customOptionInfo = hashMapOf(
            "image_fee" to product.customOptionInfo["image_fee"],
            "image_is_use" to product.customOptionInfo["image_is_use"],
            "lettering_fee" to product.customOptionInfo["lettering_fee"],
            "lettering_is_use" to product.customOptionInfo["lettering_is_use"]
        )

        val productMap = hashMapOf(
            "brand" to product.brand,
            "category" to product.category,
            "customOptionInfo" to customOptionInfo,
            "detail" to product.detail,
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
            "is_custom" to product.isCustom,
            "name" to product.name,
            "out_of_stock" to product.state,
            "price" to product.price,
            "sales_quantity" to product.salesQuantity,
            "seller_id" to product.sellerId,
            "thumbnail_image" to product.thumbnail_image,
            "update_date" to getMillisec()
        )

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