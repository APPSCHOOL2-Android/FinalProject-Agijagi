package likelion.project.agijagi.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import likelion.project.agijagi.MainActivity.Companion.displayDialogUserNotLogin
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentCustomProductDetailBinding
import likelion.project.agijagi.model.UserModel
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

        val productId = getCustomProductId()

        loadCustomProductDataAndInitViews(productId)
        setupFloorPlanDownloadButton()
        setupFavoriteButton(productId)

    }

    private fun getCustomProductId(): String {
        return arguments?.getString("prodId").toString()
    }

    private fun loadCustomProductDataAndInitViews(productId: String) {
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

            startShimmerAnimations(
                shimmerLayoutCustomProductDetailThumbnailImage,
                shimmerLayoutImages
            )


            db.collection("product").document(productId).get().addOnSuccessListener {
                val thumbnailImage = it["thumbnail_image"].toString()
                val brand = it["brand"].toString()
                val name = it["name"].toString()
                val price = "${dec.format(it["price"].toString().toLong())}원"
                val detail = it["detail"].toString()
                val image = it["image"] as ArrayList<*>
                val state = it["state"].toString()

                storageRef.child(thumbnailImage).downloadUrl.addOnSuccessListener { thumbnailUri ->
                    shimmerLayoutCustomProductDetailThumbnailImage.stopShimmerAnimation()
                    Glide.with(this@CustomProductDetailFragment)
                        .load(thumbnailUri)
                        .placeholder(R.drawable.product_detail_default_image)
                        .into(imageviewCustomProductDetailThumbnailImage)

                    displayProductInfo(brand, name, price, name, detail)

                    loadProductImages(image, shimmerLayoutImages, imageViews, productId, state)
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
        imageViews: List<ImageView>,
        productId: String,
        state: String
    ) {
        for (idx in 0 until image.size) {
            storageRef.child(image[idx].toString()).downloadUrl.addOnSuccessListener { imageUri ->
                shimmerLayoutImages[idx].stopShimmerAnimation()

                Glide.with(this@CustomProductDetailFragment)
                    .load(imageUri)
                    .placeholder(R.drawable.product_detail_default_image)
                    .into(imageViews[idx])

                setupPurchaseButton(productId, state)
                setupToolbar()
                setupFloatingButton()
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
                if (UserModel.uid == "") {
                    displayDialogUserNotLogin(
                        requireContext(),
                        findNavController(),
                        R.id.action_customProductDetailFragment_to_loginFragment
                    )
                } else {
                    when (it.itemId) {
                        R.id.menu_product_detail_shopping -> {
                            findNavController().navigate(R.id.action_customProductDetailFragment_to_shoppingListFragment)
                        }
                    }
                }
                false
            }
        }
    }

    private fun setupFloatingButton() {
        binding.customFloatingButtonCustomProductDetailToChatting.customFloatingButtonLayout.setOnClickListener {
            if (UserModel.uid == "") {
                displayDialogUserNotLogin(
                    requireContext(),
                    findNavController(),
                    R.id.action_customProductDetailFragment_to_loginFragment
                )
            } else {
                findNavController()
                    .navigate(R.id.action_customProductDetailFragment_to_chattingListFragment)
            }
        }
    }

    private fun setupFloorPlanDownloadButton() {
        binding.buttonCustomProductDetailDownloadFloorPlan.setOnClickListener {
            Snackbar.make(it, "도면 다운로드가 완료되었습니다.", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupFavoriteButton(productId: String) {
        val buyerId = UserModel.roleId
        binding.imageButtonCustomProductDetailFavorite.run {
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
                        R.id.action_customProductDetailFragment_to_loginFragment
                    )
                } else {
                    it.isSelected = it.isSelected != true
                    if (it.isSelected) {
                        val product = hashMapOf("prodId" to productId)
                        db.collection("buyer")
                            .document(buyerId)
                            .collection("wish")
                            .document(productId)
                            .set(product)
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
        binding.buttonCustomProductDetailPurchase.run {
            if (state == "품절") {
                setBackgroundResource(R.drawable.wide_box_rounded_purchase_button_inactive)
                setTextColor(ContextCompat.getColor(context, R.color.jagi_black_42))
                text = "품절"
            }
            setOnClickListener {
                if (state == "품절") {
                    Snackbar.make(binding.root, "품절된 상품입니다.", Snackbar.LENGTH_SHORT).show()
                } else if (UserModel.uid == "") {
                    displayDialogUserNotLogin(
                        requireContext(),
                        findNavController(),
                        R.id.action_customProductDetailFragment_to_loginFragment
                    )
                } else {
                    val bundle = bundleOf("prodId" to productId)
                    it.findNavController()
                        .navigate(
                            R.id.action_customProductDetailFragment_to_customOptionFragment,
                            bundle
                        )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}