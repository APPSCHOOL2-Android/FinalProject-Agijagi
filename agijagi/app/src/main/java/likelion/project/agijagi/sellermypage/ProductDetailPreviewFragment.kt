package likelion.project.agijagi.sellermypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import likelion.project.agijagi.MainActivity.Companion.getMilliSec
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentProductDetailPreviewBinding
import likelion.project.agijagi.model.ProductModel
import likelion.project.agijagi.model.SellerModel
import likelion.project.agijagi.model.UserModel
import java.text.DecimalFormat

class ProductDetailPreviewFragment : Fragment() {

    private var _binding: FragmentProductDetailPreviewBinding? = null
    private val binding get() = _binding!!

    lateinit var product: ProductModel
    val dec = DecimalFormat("#,###")

    val db = Firebase.firestore
    private val storageRef = Firebase.storage.reference

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
            Glide.with(this@ProductDetailPreviewFragment)
                .load(product.thumbnail_image)
                .placeholder(R.drawable.product_detail_default_image)
                .into(imageviewProductDetailPreviewThumbnailImage)

            val imageViews = listOf(
                imageviewProductPreviewDetailImage1,
                imageviewProductPreviewDetailImage2,
                imageviewProductPreviewDetailImage3,
                imageviewProductPreviewDetailImage4,
                imageviewProductPreviewDetailImage5,
                imageviewProductPreviewDetailImage6
            )

            product.image.take(imageViews.size).forEachIndexed { index, imageUrl ->
                Glide.with(this@ProductDetailPreviewFragment)
                    .load(imageUrl)
                    .placeholder(R.drawable.product_detail_default_image)
                    .into(imageViews[index])
            }

            imageViews.subList(product.image.size, imageViews.size).forEach { imageView ->
                imageView.visibility = GONE
            }

            textviewProductDetailPreviewBrand.text = SellerModel.businessName
            textviewProductDetailPreviewName.text = product.name
            "${dec.format(product.price.toLong())}원".also {
                textviewProductDetailPreviewPrice.text = it
            }
            textviewProductDetailPreviewInfoTitle.text = product.name
            textviewProductDetailPreviewInfoDescription.text = product.detail
        }
    }

    private fun uploadProductImages(productId: String) {
        Snackbar.make(requireView(), "상품 등록 중...", Snackbar.LENGTH_SHORT).show()
        val productThumbnailImageFileName = "productImage/$productId/thumbnail_${getMilliSec()}.jpg"
        storageRef.child(productThumbnailImageFileName).putFile(product.thumbnail_image.toUri())
            .addOnSuccessListener {
                product.thumbnail_image = productThumbnailImageFileName

                val productImageFileNameList = arrayListOf<String>()
                product.image.take(product.image.size).forEach {
                    val productImageFileName = "productImage/$productId/${getMilliSec()}.jpg"
                    productImageFileNameList.add(productImageFileName)
                    storageRef.child(productImageFileName).putFile(it.toUri())
                        .addOnSuccessListener {
                            uploadProductFloorPlans(productId)
                        }
                }
                product.image = productImageFileNameList
            }
    }

    private fun uploadProductFloorPlans(productId: String) {
        if (!product.isCustom) {
            registerProductData(productId)
        } else {
            val productFloorPlanFileNameList = arrayListOf<String>()
            product.floorPlan.take(product.floorPlan.size).forEach {
                val productFloorPlanFileName = "productFloorPlan/$productId/${getMilliSec()}.jpg"
                productFloorPlanFileNameList.add(productFloorPlanFileName)
                storageRef.child(productFloorPlanFileName).putFile(it.toUri())
                    .addOnSuccessListener {
                        registerProductData(productId)
                    }
            }
            product.floorPlan = productFloorPlanFileNameList
        }
    }

    private fun registerProductData(productId: String) {
        product.state = "판매"
        val customOptionInfo = hashMapOf(
            "image_fee" to product.customOptionInfo["image_fee"],
            "image_is_use" to product.customOptionInfo["image_is_use"],
            "lettering_fee" to product.customOptionInfo["lettering_fee"],
            "lettering_is_use" to product.customOptionInfo["lettering_is_use"]
        )

        val productMap = hashMapOf(
            "brand" to SellerModel.businessName,
            "category" to product.category,
            "customOptionInfo" to customOptionInfo,
            "detail" to product.detail,
            "floor_plan" to product.floorPlan,
            "image" to product.image,
            "is_custom" to product.isCustom,
            "is_delete" to product.isDelete,
            "name" to product.name,
            "state" to product.state,
            "price" to product.price,
            "sales_quantity" to product.salesQuantity,
            "seller_id" to UserModel.roleId,
            "shopping_quantity" to product.shoppingQuantity,
            "thumbnail_image" to product.thumbnail_image,
            "update_date" to getMilliSec()
        )
        db.collection("product").document(productId).set(productMap).addOnSuccessListener {
            findNavController().navigate(R.id.action_productDetailPreviewFragment_to_productListFragment)
        }
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
                val productId = getMilliSec()
                uploadProductImages(productId)

//                activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
//                Snackbar.make(it, "상품 등록이 완료되었습니다.", Snackbar.LENGTH_SHORT)
//                    .setAnchorView(binding.buttonProductDetailPreviewProductRegistration)
//                    .addCallback(object : Snackbar.Callback() {
//                        override fun onDismissed(
//                            transientBottomBar: Snackbar?,
//                            event: Int
//                        ) {
//                            super.onDismissed(transientBottomBar, event)
//                            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
//                            // 화면이동
//                            findNavController().navigate(R.id.action_productDetailPreviewFragment_to_productListFragment)
//                        }
//                    })
//                    .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}