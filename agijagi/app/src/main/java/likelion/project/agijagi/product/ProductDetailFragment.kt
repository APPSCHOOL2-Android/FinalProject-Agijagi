package likelion.project.agijagi.product

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.supercharge.shimmerlayout.ShimmerLayout
import likelion.project.agijagi.MainActivity.Companion.displayDialogUserNotLogin
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentProductDetailBinding
import likelion.project.agijagi.model.UserModel
import java.text.DecimalFormat

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    val dec = DecimalFormat("#,###")

    val db = Firebase.firestore
    private val storageRef = Firebase.storage.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productId = getProductId()

        Log.d("hye", productId)
        loadProductDataAndInitViews(productId)
        setupToolbar()
        setupFavoriteButton(productId)
    }

    private fun getProductId(): String {
        return arguments?.getString("prodId").toString()
    }

    private fun loadProductDataAndInitViews(productId: String) {
        binding.run {
            val shimmerLayoutImages = listOf(
                shimmerLayoutProductDetailImage1,
                shimmerLayoutProductDetailImage2,
                shimmerLayoutProductDetailImage3,
                shimmerLayoutProductDetailImage4,
                shimmerLayoutProductDetailImage5,
                shimmerLayoutProductDetailImage6
            )

            val imageViews = listOf(
                imageviewProductDetailImage1,
                imageviewProductDetailImage2,
                imageviewProductDetailImage3,
                imageviewProductDetailImage4,
                imageviewProductDetailImage5,
                imageviewProductDetailImage6
            )

            startShimmerAnimations(shimmerLayoutProductDetailThumbnailImage, shimmerLayoutImages)

            db.collection("product").document(productId).get().addOnSuccessListener {
                val thumbnailImage = it["thumbnail_image"].toString()
                val brand = it["brand"].toString()
                val name = it["name"].toString()
                val price = "${dec.format(it["price"].toString().toLong())}원"
                val detail = it["detail"].toString()
                val image = it["image"] as ArrayList<*>
                val state = it["state"].toString()

                storageRef.child(thumbnailImage).downloadUrl.addOnSuccessListener { thumbnailUri ->
                    shimmerLayoutProductDetailThumbnailImage.stopShimmerAnimation()
                    Glide.with(this@ProductDetailFragment)
                        .load(thumbnailUri)
                        .placeholder(R.drawable.product_detail_default_image)
                        .into(imageviewProductDetailThumbnailImage)

                    displayProductInfo(brand, name, price, name, detail)

                    loadProductImages(image, shimmerLayoutImages, imageViews)

                    setupPurchaseButton(productId, state)
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
            textviewProductDetailBrand.text = brand
            textviewProductDetailName.text = name
            textviewProductDetailPrice.text = price
            textviewProductDetailInfoTitle.text = title
            textviewProductDetailInfoDescription.text = detail
        }
    }

    private fun loadProductImages(
        image: ArrayList<*>,
        shimmerLayoutImages: List<ShimmerLayout>,
        imageViews: List<ImageView>
    ) {
        for (idx in 0 until image.size) {
            storageRef.child(image[idx].toString()).downloadUrl.addOnSuccessListener { imageUri ->
                shimmerLayoutImages[idx].stopShimmerAnimation()

                Glide.with(this@ProductDetailFragment)
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
        binding.toolbarProductDetail.run {
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            setOnMenuItemClickListener {
                if (UserModel.uid == "") {
                    displayDialogUserNotLogin(
                        requireContext(),
                        findNavController(),
                        R.id.action_productDetailFragment_to_loginFragment
                    )
                } else {
                    when (it.itemId) {
                        R.id.menu_product_detail_shopping -> {
                            findNavController().navigate(R.id.action_productDetailFragment_to_shoppingListFragment)
                        }
                    }
                }
                false
            }
        }
    }

    private fun setupFavoriteButton(productId: String) {
        val buyerId = UserModel.roleId
        binding.imageButtonProductDetailFavorite.run {
            if (UserModel.uid != "") {
                db.collection("buyer")
                    .document(buyerId)
                    .collection("wish")
                    .get()
                    .addOnSuccessListener {
                        for (document in it) {
                            if (document.id == productId) {
                                this.isSelected = true
                            }
                        }
                    }
            }

            setOnClickListener {
                if (UserModel.uid == "") {
                    displayDialogUserNotLogin(
                        requireContext(),
                        findNavController(),
                        R.id.action_productDetailFragment_to_loginFragment
                    )
                } else {
                    it.isSelected = it.isSelected != true
                    if (it.isSelected) {
                        val prodId = hashMapOf("prodId" to productId)
                        db.collection("buyer")
                            .document(buyerId)
                            .collection("wish")
                            .document(productId)
                            .set(prodId)
                    } else {
                        db.collection("buyer")
                            .document(buyerId)
                            .collection("wish")
                            .document(productId)
                            .delete()
                    }
                }
            }
        }
    }

    private fun setupPurchaseButton(productId: String, state: String) {
        binding.buttonProductDetailPurchase.run {
            if (state == "품절") {
                setBackgroundResource(R.drawable.wide_box_rounded_purchase_button_inactive)
                text = "품절"
            }
            setOnClickListener {
                if (state == "품절") {
                    Snackbar.make(binding.root, "품절된 상품입니다.", Snackbar.LENGTH_SHORT).show()
                } else if (UserModel.uid == "") {
                    displayDialogUserNotLogin(
                        requireContext(),
                        findNavController(),
                        R.id.action_productDetailFragment_to_loginFragment
                    )
                } else {
                    val bundle = bundleOf("prodId" to productId)
                    it.findNavController()
                        .navigate(R.id.action_productDetailFragment_to_readyMadeOptionFragment, bundle)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

