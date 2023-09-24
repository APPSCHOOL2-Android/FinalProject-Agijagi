package likelion.project.agijagi.sellermypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
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

class ProductUpdateDetailPreviewFragment : Fragment() {

    private var _binding: FragmentProductDetailPreviewBinding? = null
    private val binding get() = _binding!!

    lateinit var product: ProductModel
    val dec = DecimalFormat("#,###")

    val db = Firebase.firestore
    private val storageRef = Firebase.storage.reference

    private lateinit var newThumbnailImage: String
    var newImageList = mutableListOf<String>()

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
        setToolbarItemAction()
        setProductUpdateButton()
    }

    private fun getProductDataFromArguments() {
        val bundle = arguments

        if (bundle != null) {
            product = bundle.getParcelable("productData")!!
        }
    }

    private fun getProductId(): String {
        return arguments?.getString("prodId").toString()
    }

    private fun initViews() {
        binding.run {
            if (product.thumbnail_image.startsWith("productImage/")) {
                storageRef.child(product.thumbnail_image).downloadUrl.addOnSuccessListener {
                    Glide.with(this@ProductUpdateDetailPreviewFragment)
                        .load(it)
                        .placeholder(R.drawable.product_detail_default_image)
                        .into(imageviewProductDetailPreviewThumbnailImage)
                    newThumbnailImage = it.toString()
                }
            } else if (product.thumbnail_image == "") {
                if (product.image[0].startsWith("productImage/")) {
                    storageRef.child(product.image[0]).downloadUrl.addOnSuccessListener {
                        Glide.with(this@ProductUpdateDetailPreviewFragment)
                            .load(it)
                            .placeholder(R.drawable.product_detail_default_image)
                            .into(imageviewProductDetailPreviewThumbnailImage)
                        newThumbnailImage = it.toString()
                    }
                } else {
                    Glide.with(this@ProductUpdateDetailPreviewFragment)
                        .load(product.image[0])
                        .placeholder(R.drawable.product_detail_default_image)
                        .into(imageviewProductDetailPreviewThumbnailImage)
                    newThumbnailImage = product.image[0]
                }
            } else {
                Glide.with(this@ProductUpdateDetailPreviewFragment)
                    .load(product.thumbnail_image)
                    .placeholder(R.drawable.product_detail_default_image)
                    .into(imageviewProductDetailPreviewThumbnailImage)
                newThumbnailImage = product.thumbnail_image
            }

            val imageViews = listOf(
                imageviewProductPreviewDetailImage1,
                imageviewProductPreviewDetailImage2,
                imageviewProductPreviewDetailImage3,
                imageviewProductPreviewDetailImage4,
                imageviewProductPreviewDetailImage5,
                imageviewProductPreviewDetailImage6
            )

            product.image.take(imageViews.size).forEachIndexed { index, imageUrl ->
                if (product.image[index].startsWith("productImage/")) {
                    storageRef.child(product.image[index]).downloadUrl.addOnSuccessListener {
                        Glide.with(this@ProductUpdateDetailPreviewFragment)
                            .load(it)
                            .placeholder(R.drawable.product_detail_default_image)
                            .into(imageViews[index])
                        newImageList.add(it.toString())
                    }
                } else {
                    Glide.with(this@ProductUpdateDetailPreviewFragment)
                        .load(imageUrl)
                        .placeholder(R.drawable.product_detail_default_image)
                        .into(imageViews[index])
                    newImageList.add(imageUrl)
                }
            }

            imageViews.subList(product.image.size, imageViews.size).forEach { imageView ->
                imageView.visibility = View.GONE
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
        val productThumbnailImageFileName =
            "productImage/$productId/thumbnail_${getMilliSec()}.jpg"
        storageRef.child(productThumbnailImageFileName).putFile(newThumbnailImage.toUri())
            .addOnSuccessListener {
                product.thumbnail_image = productThumbnailImageFileName

                val productImageFileNameList = arrayListOf<String>()
                newImageList.take(newImageList.size).forEach {
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

        val productMap = hashMapOf<String, Any>(
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
        db.collection("product").document(productId).set(productMap)
    }

    private fun setToolbarItemAction() {
        binding.toolbarProductDetailPreview.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setProductUpdateButton() {
        binding.buttonProductDetailPreviewProductRegistration.run {
            text = "수정"
            setOnClickListener {
                uploadProductImages(getProductId())

                activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                Snackbar.make(it, "상품 수정이 완료되었습니다.", Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.buttonProductDetailPreviewProductRegistration)
                    .addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(
                            transientBottomBar: Snackbar?,
                            event: Int
                        ) {
                            super.onDismissed(transientBottomBar, event)
                            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            // 화면이동
                            findNavController().navigate(R.id.action_productUpdateDetailPreviewFragment_to_productListFragment)
                        }
                    })
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}