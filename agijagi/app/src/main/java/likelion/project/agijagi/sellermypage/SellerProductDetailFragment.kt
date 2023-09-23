package likelion.project.agijagi.sellermypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.supercharge.shimmerlayout.ShimmerLayout
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentSellerProductDetailBinding
import likelion.project.agijagi.model.ProductModel
import java.text.DecimalFormat

class SellerProductDetailFragment : Fragment() {

    private var _binding: FragmentSellerProductDetailBinding? = null
    private val binding get() = _binding!!

    val dec = DecimalFormat("#,###")

    val db = Firebase.firestore
    private val storageRef = Firebase.storage.reference

    private var product: ProductModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerProductDetailBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productId = getProductId()

        loadProductDataAndInitViews(productId)
        setupToolbar()
    }

    private fun getProductId(): String {
        return arguments?.getString("prodId").toString()
    }

    private fun loadProductDataAndInitViews(productId: String) {
        binding.run {
            val shimmerLayoutImages = listOf(
                shimmerLayoutSellerProductDetailImage1,
                shimmerLayoutSellerProductDetailImage2,
                shimmerLayoutSellerProductDetailImage3,
                shimmerLayoutSellerProductDetailImage4,
                shimmerLayoutSellerProductDetailImage5,
                shimmerLayoutSellerProductDetailImage6
            )

            val imageViews = listOf(
                imageviewSellerProductDetailImage1,
                imageviewSellerProductDetailImage2,
                imageviewSellerProductDetailImage3,
                imageviewSellerProductDetailImage4,
                imageviewSellerProductDetailImage5,
                imageviewSellerProductDetailImage6
            )

            startShimmerAnimations(
                shimmerLayoutSellerProductDetailThumbnailImage,
                shimmerLayoutImages
            )

            db.collection("product").document(productId).get().addOnSuccessListener {
                val thumbnailImage = it["thumbnail_image"].toString()
                val brand = it["brand"].toString()
                val name = it["name"].toString()
                val price = it["price"].toString()
                val detail = it["detail"].toString()
                val image = it.get("image") as ArrayList<String>
                val category = it["category"].toString()
                val customOptionInfo = it.get("customOptionInfo") as HashMap<String, String>
                val floorPlan = it.get("floor_plan") as ArrayList<String>
                val isCustom = it.getBoolean("is_custom")!!
                val isDelete = it.getBoolean("is_delete")!!
                val salesQuantity = it["sales_quantity"].toString().toLong()
                val sellerId = it["seller_id"].toString()
                val shoppingQuantity = it["shopping_quantity"].toString().toLong()
                val state = it["state"].toString()
                val updateDate = it["updateDate"].toString()

                product = ProductModel(
                    productId,
                    brand,
                    category,
                    customOptionInfo,
                    detail,
                    floorPlan,
                    image,
                    isCustom,
                    isDelete,
                    name,
                    state,
                    price,
                    salesQuantity,
                    sellerId,
                    shoppingQuantity,
                    updateDate
                )

                storageRef.child(thumbnailImage).downloadUrl.addOnSuccessListener { thumbnailUri ->
                    binding.run {
                        shimmerLayoutSellerProductDetailThumbnailImage.stopShimmerAnimation()
                        Glide.with(this@SellerProductDetailFragment)
                            .load(thumbnailUri)
                            .placeholder(R.drawable.product_detail_default_image)
                            .into(imageviewSellerProductDetailThumbnailImage)

                        displayProductInfo(brand, name, price, name, detail)

                        loadProductImages(image, shimmerLayoutImages, imageViews)
                    }
                }
            }
        }
    }

    private fun startShimmerAnimations(
        shimmerLayoutThumbnailImage: ShimmerLayout,
        shimmerLayoutImages: List<ShimmerLayout>
    ) {
        shimmerLayoutThumbnailImage.startShimmerAnimation()
        shimmerLayoutImages.forEach {
            it.startShimmerAnimation()
        }
    }

    private fun displayProductInfo(
        brand: String,
        name: String,
        price: String,
        title: String,
        detail: String
    ) {
        binding.run {
            textviewSellerProductDetailBrand.text = brand
            textviewSellerProductDetailName.text = name
            "${dec.format(price.toLong())}원".also {
                textviewSellerProductDetailPrice.text = it
            }
            textviewSellerProductDetailInfoTitle.text = title
            textviewSellerProductDetailInfoDescription.text = detail
        }
    }

    private fun loadProductImages(
        image: ArrayList<*>,
        shimmerLayoutImages: List<ShimmerLayout>,
        imageViews: List<ImageView>
    ) {
        val imageUriString = ArrayList<String>()
        for (idx in 0 until image.size) {
            storageRef.child(image[idx].toString()).downloadUrl.addOnSuccessListener { imageUri ->
                shimmerLayoutImages[idx].stopShimmerAnimation()
                imageUriString.add(imageUri.toString())

                Glide.with(this@SellerProductDetailFragment)
                    .load(imageUri)
                    .placeholder(R.drawable.product_detail_default_image)
                    .into(imageViews[idx])
            }
        }

        imageViews.subList(image.size, imageViews.size).forEach { imageView ->
            imageView.visibility = View.GONE
        }
    }

    private fun setupToolbar() {
        binding.toolbarSellerProductDetail.run {
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_product_detail_edit -> {
                        val bundle = Bundle()
                        bundle.putParcelable("productData", product)
                        bundle.putString("prodId", getProductId())
                        findNavController().navigate(
                            R.id.action_sellerProductDetailFragment_to_productUpdateFragment,
                            bundle
                        )
                    }

                    R.id.menu_product_detail_delete -> {
                        val isDeleteState = hashMapOf<String, Any>(
                            "is_delete" to true
                        )
                        db.collection("product")
                            .document(getProductId())
                            .update(isDeleteState)
                            .addOnSuccessListener {
                                findNavController().popBackStack()
                            }
                    }
                }
                false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}