package likelion.project.agijagi.productoption

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.drawToBitmap
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentCustomOptionBinding

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

    private var lettering = false
    private var image = false

    private var frontImageState = false
    private var backImageState = false
    private var leftImageState = false
    private var rightImageState = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomOptionBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        // 앨범 설정
        frontAlbumLauncher = albumSetting(binding.customOptionFront.itemCustomOptionImage)
        backAlbumLauncher = albumSetting(binding.customOptionBack.itemCustomOptionImage)
        leftAlbumLauncher = albumSetting(binding.customOptionLeft.itemCustomOptionImage)
        rightAlbumLauncher = albumSetting(binding.customOptionRight.itemCustomOptionImage)

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

        // 구매버튼
        setPurchaseButton()
        // 장바구니 버튼
        setShoppingBagButton()
    }

    private fun setCustomOptions() {
        binding.run {
            menuCustomOptionSelectText.setOnItemClickListener { adapterView, view, i, l ->
                when (i) {
                    MenuOption.MENU_LETTERING.idx -> {
                        lettering = true
                        image = false
                        Log.d("Lettering", lettering.toString())
                        layoutCustomLetteringOption.isVisible = true
                        layoutCustomPrintOption.isGone = true
                    }

                    MenuOption.MENU_IMAGE.idx -> {
                        lettering = false
                        image = true
                        layoutCustomLetteringOption.isGone = true
                        layoutCustomPrintOption.isVisible = true
                    }
                }
            }
        }
    }

    private fun getImage(imageState: Boolean, imageView: ImageView) = if (imageState) {
        imageView.drawable.toBitmap()
    } else null

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

    private fun setShoppingBagButton() {
        binding.imageButtonCustomOptionShoppingBag.setOnClickListener {
            findNavController().navigate(R.id.action_customOptionFragment_to_shoppingListFragment)
        }
    }

    private fun setPurchaseButton() {
        binding.run {
            buttonCustomOptionPurchase.setOnClickListener {
                // 데이터가져오기
                if (lettering) {
                    val customWord = editInputCustomOptionText.text.toString()
                    val customLocation = editInputCustomOptionLocationText.text.toString()
                    val count = editInputCustomOptionVolumeText.text.toString().toInt()
                    val letteringCustomOption = CustomOptionModel(
                        "lettering",
                        customWord,
                        customLocation,
                        null, null, null, null,
                        count
                    )
                    val bundle = Bundle().apply {
                        putParcelable("lettering", letteringCustomOption)
                    }
                    findNavController().navigate(
                        R.id.action_customOptionFragment_to_paymentFragment,
                        bundle
                    )
                }
                if (image) {
                    val frontImage =
                        getImage(frontImageState, binding.customOptionFront.itemCustomOptionImage)
                    val backImage =
                        getImage(backImageState, binding.customOptionBack.itemCustomOptionImage)
                    val leftImage =
                        getImage(backImageState, binding.customOptionBack.itemCustomOptionImage)
                    val rightImage =
                        getImage(backImageState, binding.customOptionBack.itemCustomOptionImage)
                    binding.customOptionFront.itemCustomOptionImage
                    val count = editInputCustomOptionVolumeText.text.toString().toInt()

                    val imageCustomOption = CustomOptionModel(
                        "image",
                        null,
                        null,
                        frontImage,
                        backImage,
                        leftImage,
                        rightImage,
                        count
                    )
                    val bundle = Bundle().apply {
                        putParcelable("image", imageCustomOption)
                    }
                    findNavController().navigate(
                        R.id.action_customOptionFragment_to_paymentFragment,
                        bundle
                    )
                }
            }
        }
    }

    // 앨범 관련 설정
    fun albumSetting(previewImageView: ImageView): ActivityResultLauncher<Intent> {
        val albumContract = ActivityResultContracts.StartActivityForResult()
        val albumLauncher = registerForActivityResult(albumContract) {

            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                // 선택한 이미지에 접근할 수 있는 Uri 객체를 추출한다.
                if (it.data?.data != null) {
                    uploadUri = it.data?.data

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    enum class MenuOption(val idx: Int, val string: String) {
        MENU_LETTERING(0, "lettering"),
        MENU_IMAGE(1, "image")
    }
}