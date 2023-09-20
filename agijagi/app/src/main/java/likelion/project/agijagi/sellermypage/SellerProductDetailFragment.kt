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
import java.text.DecimalFormat

class SellerProductDetailFragment : Fragment() {

    private var _binding: FragmentSellerProductDetailBinding? = null
    private val binding get() = _binding!!

    val dec = DecimalFormat("#,###")

    val db = Firebase.firestore
    private val storageRef = Firebase.storage.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerProductDetailBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProductDataAndInitViews()
        setupToolbar()
    }

    private fun loadProductDataAndInitViews() {
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

            startShimmerAnimations(shimmerLayoutSellerProductDetailThumbnailImage, shimmerLayoutImages)

            db.collection("product").document("230916094337474").get().addOnSuccessListener {
                val thumbnailImage = it.getString("thumbnail_image").toString()
                val brand = it.getString("brand").toString()
                val name = it.getString("name").toString()
                val price = it.getString("price").toString()
                val detail = it.getString("detail").toString()
                val image = it.get("image") as ArrayList<*>

                storageRef.child(thumbnailImage).downloadUrl.addOnSuccessListener { thumbnailUri ->
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
            textviewSellerProductDetailPrice.text = price
            textviewSellerProductDetailInfoTitle.text = title
            textviewSellerProductDetailInfoDescription.text = detail
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
                        findNavController().navigate(R.id.action_sellerProductDetailFragment_to_productUpdateFragment)
                    }

                    R.id.menu_product_detail_delete -> {

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