package likelion.project.agijagi.productoption

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentCustomOptionBinding
import likelion.project.agijagi.model.ProdInfo
import likelion.project.agijagi.model.UserModel
import java.text.DecimalFormat

class CustomOptionFragment : Fragment() {
    private var _binding: FragmentCustomOptionBinding? = null
    private val binding get() = _binding!!
    lateinit var mainActivity: MainActivity

    // 업로드할 이미지의 Uri
    var uploadUri: Uri? = null

    lateinit var frontAlbumLauncher: ActivityResultLauncher<Intent>
    lateinit var backAlbumLauncher: ActivityResultLauncher<Intent>
    lateinit var leftAlbumLauncher: ActivityResultLauncher<Intent>
    lateinit var rightAlbumLauncher: ActivityResultLauncher<Intent>

    private var itemState = MenuOption.MENU_LETTERING

    private var frontImageState = false
    private var backImageState = false
    private var leftImageState = false
    private var rightImageState = false

    private lateinit var productId: String
    private lateinit var productHashMap: HashMap<String, Any?>

    private var frontImage: String? = null
    private var backImage: String? = null
    private var leftImage: String? = null
    private var rightImage: String? = null

    private val db = Firebase.firestore
    private val storageRef = Firebase.storage.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomOptionBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        // 앨범 설정
        frontAlbumLauncher = albumSetting(binding.customOptionFront.itemCustomOptionImage, 1)
        backAlbumLauncher = albumSetting(binding.customOptionBack.itemCustomOptionImage, 2)
        leftAlbumLauncher = albumSetting(binding.customOptionLeft.itemCustomOptionImage, 3)
        rightAlbumLauncher = albumSetting(binding.customOptionRight.itemCustomOptionImage, 4)

        // productDetailFragment 에서 Id 값을 받아옴
        productId = arguments?.getString("prodId").toString()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 커스텀 옵션 선택
        setCustomOptions()

        // 이미지 불러오기
        setFrontCustomImage()
        setBackCustomImage()
        setLeftCustomImage()
        setRightCustomImage()

        // 도자기 정보를 받아 출력
        getData()
    }

    private fun getData() {
        val dec = DecimalFormat("#,###")
        showSampleData(true)
        db.collection("product").document(productId).get().addOnSuccessListener {
            val thumbnailImage = it["thumbnail_image"].toString()
            binding.textviewShoppingListItemBrand.text = it["brand"].toString()
            binding.textviewShoppingListItemName.text = it["name"].toString()
            binding.textviewShoppingListItemPrice.text = "${dec.format(it["price"].toString().toLong())}원"

            storageRef.child(thumbnailImage).downloadUrl.addOnSuccessListener { thumbnailUri ->
                Glide.with(this@CustomOptionFragment)
                    .load(thumbnailUri)
                    .placeholder(R.drawable.product_detail_default_image)
                    .into(binding.imageviewShoppingListItem)
            }

            showSampleData(false)
            // 구매버튼
            setPurchaseButton()
            // 장바구니 버튼
            setShoppingBagButton()
            // 뒤로가기 버튼
            setToolbarItemAction()
        }
    }

    private fun showSampleData(isLoading: Boolean) {
        if (isLoading) {
            binding.containerItemShimmer.startShimmer()
            binding.containerItemShimmer.visibility = View.VISIBLE
            binding.containerItem.visibility = View.GONE
        } else {
            binding.containerItemShimmer.stopShimmer()
            binding.containerItemShimmer.visibility = View.GONE
            binding.containerItem.visibility = View.VISIBLE
        }
    }

    private fun setCustomOptions() {
        binding.run {
            menuCustomOptionSelectText.setOnItemClickListener { adapterView, view, i, l ->
                when (i) {
                    MenuOption.MENU_LETTERING.idx -> {
                        itemState = MenuOption.MENU_LETTERING
                        layoutCustomLetteringOption.isVisible = true
                        layoutCustomPrintOption.isGone = true
                    }

                    MenuOption.MENU_IMAGE.idx -> {
                        itemState = MenuOption.MENU_IMAGE
                        layoutCustomLetteringOption.isGone = true
                        layoutCustomPrintOption.isVisible = true
                    }
                }
            }
        }
    }

    private fun setFrontCustomImage() {
        binding.customOptionFront.run {
            itemCustomOptionImage.setOnClickListener {
                val newIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                newIntent.setType("image/*")
                val mimeType = arrayOf("image/*")

                newIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
                frontAlbumLauncher.launch(newIntent)
                frontImageState = true
            }

            buttonCancel.setOnClickListener {
                itemCustomOptionImage.setImageResource(R.drawable.front_side_no_stroke)
                frontImageState = false
            }
        }
    }

    private fun setBackCustomImage() {
        binding.customOptionBack.run {
            itemCustomOptionImage.setOnClickListener {
                val newIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                newIntent.setType("image/*")
                val mimeType = arrayOf("image/*")

                newIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
                backAlbumLauncher.launch(newIntent)
                backImageState = true
            }

            buttonCancel.setOnClickListener {
                itemCustomOptionImage.setImageResource(R.drawable.back_side_no_stroke)
                backImageState = false
            }
        }
    }

    private fun setLeftCustomImage() {
        binding.customOptionLeft.run {
            itemCustomOptionImage.setOnClickListener {
                val newIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                newIntent.setType("image/*")
                val mimeType = arrayOf("image/*")

                newIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
                leftAlbumLauncher.launch(newIntent)
                leftImageState = true
            }

            buttonCancel.setOnClickListener {
                itemCustomOptionImage.setImageResource(R.drawable.left_side_no_stroke)
                leftImageState = false
            }
        }
    }

    private fun setRightCustomImage() {
        binding.customOptionRight.run {
            itemCustomOptionImage.setOnClickListener {
                val newIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                newIntent.setType("image/*")
                val mimeType = arrayOf("image/*")

                newIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
                rightAlbumLauncher.launch(newIntent)
                rightImageState = true
            }

            buttonCancel.setOnClickListener {
                itemCustomOptionImage.setImageResource(R.drawable.right_side_no_stroke)
                rightImageState = false
            }
        }
    }

    private fun setToolbarItemAction() {
        binding.toolbarCustomOption.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setPurchaseButton() {
        binding.run {
            buttonCustomOptionPurchase.setOnClickListener {
                val count = editInputCustomOptionVolumeText.text.toString().toLong()
                db.collection("product").document(productId).get().addOnSuccessListener {
                    val price = it["price"].toString()
                    val bundle = when (itemState) {
                        MenuOption.MENU_LETTERING -> {
                            getLetteringData(count, price)
                        }

                        MenuOption.MENU_IMAGE -> {
                            getImageData(count)
                        }
                    }
                    findNavController().navigate(
                        R.id.action_customOptionFragment_to_paymentFragment,
                        bundle
                    )
                }
            }
        }
    }

    private fun getLetteringData(count: Long, price: String): Bundle {
        binding.run {
            val customWord = editInputCustomOptionText.text.toString()
            val customLocation = editInputCustomOptionLocationText.text.toString()

            val letteringCustomOption = ProdInfo(
                true,
                productId,
                count,
                hashMapOf(),
                "Lettering",
                price,
                customWord,
                customLocation
            )
            return Bundle().apply { putParcelable("prodInfo", letteringCustomOption) }
        }
    }

    private fun getImageData(count: Long): Bundle {

        val imageList = hashMapOf(
            "frontImage" to frontImage,
            "backImage" to backImage,
            "leftImage" to leftImage,
            "rightImage" to rightImage
        )

        val imageCustomOption = ProdInfo(
            true,
            productId,
            count,
            imageList,
            "Image",
            "",
            null, null
        )
        return Bundle().apply {
            putParcelable("prodInfo", imageCustomOption)
        }
    }

    // 앨범 관련 설정
    fun albumSetting(previewImageView: ImageView, side: Int): ActivityResultLauncher<Intent> {
        val albumContract = ActivityResultContracts.StartActivityForResult()
        val albumLauncher = registerForActivityResult(albumContract) {

            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                // 선택한 이미지에 접근할 수 있는 Uri 객체를 추출한다.
                if (it.data?.data != null) {
                    uploadUri = it.data?.data

                    when (side) {
                        1 -> frontImage = uploadUri.toString()
                        2 -> backImage = uploadUri.toString()
                        3 -> leftImage = uploadUri.toString()
                        4 -> rightImage = uploadUri.toString()
                    }

                    // 안드로이드 10 (Q) 이상이라면...
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // 이미지를 생성할 수 있는 디코더를 생성한다.
                        val source =
                            ImageDecoder.createSource(mainActivity.contentResolver, uploadUri!!)
                        // Bitmap객체를 생성한다.
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        val width = binding.customOptionFront.itemCustomOptionImage.width
                        val height = binding.customOptionFront.itemCustomOptionImage.height
                        val cropImage = Bitmap.createScaledBitmap(bitmap, width, height, true)

                        previewImageView.setImageBitmap(cropImage)
                    } else {
                        // 컨텐츠 프로바이더를 통해 이미지 데이터 정보를 가져온다.
                        val cursor =
                            mainActivity.contentResolver.query(uploadUri!!, null, null, null, null)
                        if (cursor != null) {
                            cursor.moveToNext()

                            // 이미지의 경로를 가져온다.
                            val idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                            val source = cursor.getString(idx)

                            // 이미지를 생성하여 보여준다.
                            val bitmap = BitmapFactory.decodeFile(source)
                            val width = binding.customOptionFront.itemCustomOptionImage.width
                            val height = binding.customOptionFront.itemCustomOptionImage.height
                            val cropImage = Bitmap.createScaledBitmap(bitmap, width, height, true)
                            previewImageView.setImageBitmap(cropImage)
                        }
                    }
                }
            }
        }

        return albumLauncher
    }

    private fun setShoppingBagButton() {
        binding.run {
            imageButtonCustomOptionShoppingBag.setOnClickListener {

                val count = editInputCustomOptionVolumeText.text.toString().toLong()
                val customWord = editInputCustomOptionText.text.toString()
                val customLocation = editInputCustomOptionLocationText.text.toString()

                val imageList = hashMapOf(
                    "frontImage" to frontImage,
                    "backImage" to backImage,
                    "leftImage" to leftImage,
                    "rightImage" to rightImage
                )

                db.collection("product").document(productId).get().addOnSuccessListener {
                    val shoppingQuantity = it["shopping_quantity"].toString().toLong()
                    val id = it.id
                    db.collection("product").document(productId)
                        .update("shopping_quantity", shoppingQuantity.plus(1L))

                    val price = it["price"].toString()
                    val prodInfo = if (itemState == MenuOption.MENU_LETTERING) {
                        ProdInfo(
                            true,
                            productId,
                            count,
                            hashMapOf(),
                            "Lettering",
                            price,
                            customWord,
                            customLocation
                        )
                    } else {
                        ProdInfo(
                            true,
                            productId,
                            count,
                            imageList,
                            "Image",
                            price,
                            null, null
                        )
                    }

                    productHashMap = hashMapOf(
                        "isCustom" to prodInfo.isCustom,
                        "prodInfoId" to prodInfo.prodInfoId,
                        "count" to prodInfo.count,
                        "diagram" to prodInfo.diagram,
                        "option" to prodInfo.option,
                        "price" to prodInfo.price,
                        "customWord" to prodInfo.customWord,
                        "customLocation" to prodInfo.customLocation
                    )

                    // 유저 id를 가져와서 shoppingList에 productId를 추가해준다.
                    db.collection("buyer").document(UserModel.roleId)
                        .collection("shopping_list").get().addOnSuccessListener {
                            db.collection("buyer").document(UserModel.roleId)
                                .collection("shopping_list").document().set(
                                    productHashMap
                                ).addOnSuccessListener {
                                    showSnackBar("상품이 장바구니에 추가되었습니다.")
                                    findNavController().popBackStack()
                                }
                        }
                }
            }
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    enum class MenuOption(val idx: Int, val string: String) {
        MENU_LETTERING(0, "lettering"),
        MENU_IMAGE(1, "image")
    }
}