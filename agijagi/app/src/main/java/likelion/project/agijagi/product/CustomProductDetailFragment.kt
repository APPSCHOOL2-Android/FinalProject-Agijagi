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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.supercharge.shimmerlayout.ShimmerLayout
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentCustomProductDetailBinding
import java.text.DecimalFormat

class CustomProductDetailFragment : Fragment() {

    private var _binding: FragmentCustomProductDetailBinding? = null
    private val binding get() = _binding!!

    val dec = DecimalFormat("#,###")

    val db = Firebase.firestore
    private val storageRef = Firebase.storage.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomProductDetailBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadCustomProductDataAndInitViews()
        setupToolbar()
        setupFloatingButton()
        setupFloorPlanDownloadButton()
        setupFavoriteButton()
        setupPurchaseButton()
    }

    private fun loadCustomProductDataAndInitViews() {
        binding.run {
            val shimmerLayoutImages = listOf(
                shimmerLayoutCustomProductDetailImage1,
                shimmerLayoutCustomProductDetailImage2,
                shimmerLayoutCustomProductDetailImage3,
                shimmerLayoutCustomProductDetailImage4,
                shimmerLayoutCustomProductDetailImage5,
                shimmerLayoutCustomProductDetailImage6
            )

            val imageViews = listOf(
                imageviewCustomProductDetailImage1,
                imageviewCustomProductDetailImage2,
                imageviewCustomProductDetailImage3,
                imageviewCustomProductDetailImage4,
                imageviewCustomProductDetailImage5,
                imageviewCustomProductDetailImage6
            )

            startShimmerAnimations(shimmerLayoutCustomProductDetailThumbnailImage, shimmerLayoutImages)

            db.collection("product").document("230916014552765").get().addOnSuccessListener {
                val thumbnailImage = it.getString("thumbnail_image").toString()
                val brand = it.getString("brand").toString()
                val name = it.getString("name").toString()
                val price = it.getString("price").toString()
                val detail = it.getString("detail").toString()
                val image = it.get("image") as ArrayList<*>

                storageRef.child(thumbnailImage).downloadUrl.addOnSuccessListener { thumbnailUri ->
                    shimmerLayoutCustomProductDetailThumbnailImage.stopShimmerAnimation()
                    Glide.with(this@CustomProductDetailFragment)
                        .load(thumbnailUri)
                        .placeholder(R.drawable.product_detail_default_image)
                        .into(imageviewCustomProductDetailThumbnailImage)

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
            textviewCustomProductDetailBrand.text = brand
            textviewCustomProductDetailName.text = name
            textviewCustomProductDetailPrice.text = price
            textviewCustomProductDetailInfoTitle.text = title
            textviewCustomProductDetailInfoDescription.text = detail
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

                Glide.with(this@CustomProductDetailFragment)
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
        binding.toolbarCustomProductDetail.run {
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_product_detail_shopping -> {
                        findNavController().navigate(R.id.action_customProductDetailFragment_to_shoppingListFragment)
                    }
                }
                false
            }
        }
    }

    private fun setupFloatingButton() {
        binding.customFloatingButtonCustomProductDetailToChatting.customFloatingButtonLayout.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_customProductDetailFragment_to_chattingListFragment)
        }
    }

    private fun setupFloorPlanDownloadButton() {
        binding.buttonCustomProductDetailDownloadFloorPlan.setOnClickListener {
            Snackbar.make(it, "도면 다운로드가 완료되었습니다.", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupFavoriteButton() {
        binding.imageButtonCustomProductDetailFavorite.setOnClickListener {
            it.isSelected = it.isSelected != true
        }
    }

    private fun setupPurchaseButton() {
        binding.buttonCustomProductDetailPurchase.setOnClickListener {
            val bundle = bundleOf("prodId" to "230915034613001")
            it.findNavController()
                .navigate(R.id.action_customProductDetailFragment_to_customOptionFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}