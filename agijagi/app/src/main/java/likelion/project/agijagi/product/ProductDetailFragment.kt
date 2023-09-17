package likelion.project.agijagi.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.supercharge.shimmerlayout.ShimmerLayout
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentProductDetailBinding
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

        loadProductDataAndInitViews()
        setToolbarItemAction()
        setFavoriteButton()
        setPurchaseButton()
    }

    private fun loadProductDataAndInitViews() {
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

            db.collection("product").document("230916014552765").get().addOnSuccessListener {
                val thumbnailImage = it.getString("thumbnail_image").toString()
                val brand = it.getString("brand").toString()
                val name = it.getString("name").toString()
                val price = it.getString("price").toString()
                val detail = it.getString("detail").toString()
                val image = it.get("image") as ArrayList<*>

                storageRef.child(thumbnailImage).downloadUrl.addOnSuccessListener { thumbnailUri ->
                    shimmerLayoutProductDetailThumbnailImage.stopShimmerAnimation()
                    Glide.with(this@ProductDetailFragment)
                        .load(thumbnailUri)
                        .placeholder(R.drawable.product_detail_default_image)
                        .into(imageviewProductDetailThumbnailImage)

                    displayProductInfo(brand, name, price, name, detail)

                    loadProductImages(image, shimmerLayoutImages, imageViews)
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

    private fun setToolbarItemAction() {
        binding.toolbarProductDetail.run {
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_product_detail_shopping -> {
                        findNavController().navigate(R.id.action_productDetailFragment_to_shoppingListFragment)
                    }
                }
                false
            }
        }
    }

    private fun setFavoriteButton() {
        binding.imageButtonProductDetailFavorite.setOnClickListener {
            it.isSelected = it.isSelected != true
        }
    }

    private fun setPurchaseButton() {
        binding.buttonProductDetailPurchase.setOnClickListener {
            val bundle = bundleOf("prodId" to "230915015451213")
            it.findNavController()
                .navigate(R.id.action_productDetailFragment_to_readyMadeOptionFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
