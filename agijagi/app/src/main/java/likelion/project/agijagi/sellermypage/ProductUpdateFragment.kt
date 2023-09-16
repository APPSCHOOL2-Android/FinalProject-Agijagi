package likelion.project.agijagi.sellermypage

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentProductUpdateBinding
import likelion.project.agijagi.databinding.ItemProductAddAddPictureBinding
import likelion.project.agijagi.databinding.ItemProductAddAddPlanBinding
import likelion.project.agijagi.model.ProductModel
import kotlin.concurrent.thread

class ProductUpdateFragment : Fragment() {

    private var _binding: FragmentProductUpdateBinding? = null
    private val binding get() = _binding!!
    lateinit var mainActivity: MainActivity

    lateinit var imm: InputMethodManager

    // 앨범에서 사진추가
    private val permissionList = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_MEDIA_LOCATION
    )
    lateinit var callbackActionGranted: () -> Unit
    lateinit var callbackActionDenide: () -> Unit

    var categoryIdx: Int = ListView.INVALID_POSITION

    // 사진
    lateinit var albumActivityLauncherForPictures: ActivityResultLauncher<Intent>
    private val pictureUriList: ArrayList<Uri> = arrayListOf<Uri>()
    private var pictureCheckIndex: Int = -1

    // 도면
    lateinit var albumActivityLauncherForPlans: ActivityResultLauncher<Intent>
    private val planUriList: ArrayList<Uri> = arrayListOf<Uri>()

    // 수정할 판매글의 데이터
    private lateinit var dataOrigin: ProductModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductUpdateBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        thread {
            SystemClock.sleep(500)
            // 키보드 해제
            imm = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            hideSoftKeyboard()
        }

        // 수정할 데이터 가져오기
        dataOrigin = getDataOrigin()

        // 원 게시글의 이미지 데이터 Uri 가져오기
        pictureUriList.clear()
        if (0 < dataOrigin.image.size) {
            for (i in 0 until dataOrigin.image.size) {
                pictureUriList.add(Uri.parse(dataOrigin.image[i]))
            }
        }
        planUriList.clear()
        if (0 < dataOrigin.floorPlan.size) {
            for (i in 0 until dataOrigin.floorPlan.size) {
                planUriList.add(Uri.parse(dataOrigin.floorPlan[i]))
            }
        }

        setToolbarItemAction()
        // 이미지 가져오기 런쳐 등록
        setAlbumActivityLaunchers()
        // 입력 UI 동작 등록
        setUiFunction()
        // 하단 버튼 동작 등록
        setBottomButton()
        // 이미지 뷰 초기 설정
        resetPictureView()
        resetPlanView()
    }

    private fun setToolbarItemAction() {
        binding.toolbarProductUpdate.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for (idx in 0 until permissions.size) {
            // 현재 번째의 권한 이름을 가져온다.
            val p1 = permissions[idx]
            // 권한 허용 여부 값을 가져온다.
            val g1 = grantResults[idx]

            when (p1) {
                Manifest.permission.READ_EXTERNAL_STORAGE -> {
                    if (g1 == PackageManager.PERMISSION_GRANTED) {
                        callbackActionGranted()
                    } else {
                        callbackActionDenide()
                    }
                }

                Manifest.permission.ACCESS_MEDIA_LOCATION -> {
                    if (g1 == PackageManager.PERMISSION_GRANTED) {
                        callbackActionGranted()
                    } else {
                        callbackActionDenide()
                    }
                }
            }
        }
    }

    private fun setUiFunction() {
        // 동작
        binding.run {
            // 하단 버튼 활성화 고정
            buttonProductUpdateOk.setBackgroundResource(R.drawable.wide_box_bottom_active)
            val colorInt = mainActivity.resources.getColor(R.color.jagi_ivory, null)
            buttonProductUpdateOk.setTextColor(colorInt)
            buttonProductUpdateOk.isClickable = true

            // 상품명
            editinputProductUpdateProductname.run {
                hint = dataOrigin.name
                setOnEditorActionListener { v, actionId, event ->
                    hideSoftKeyboard()
                    false
                }
            }

            // 가격
            editinputlayoutProductUpdateProductprice.run {
                hint = dataOrigin.price
                setOnEditorActionListener { v, actionId, event ->
                    hideSoftKeyboard()
                    false
                }
            }

            // 카테고리 선택
            menuProductUpdateSelectCategory.run {
                if (dataOrigin.category != "") {
                    setText(dataOrigin.category, false)

                    categoryIdx = resources.getStringArray(R.array.product_add_category).toList()
                        .indexOf(dataOrigin.category)
                }

                onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
                    categoryIdx = position
                    hideSoftKeyboard()
                }
            }

            // 옵션1 체크박스
            checkBoxProductUpdateOption1.run {
                isChecked = dataOrigin.customOptionInfo["lettering_is_use"].toBoolean()

                if (isChecked) {
                    editinputlayoutProductUpdateOption1Price.hint =
                        dataOrigin.customOptionInfo["lettering_fee"]
                }
            }

            // 옵션1 가격입력
            editinputlayoutProductUpdateOption1Price.run {
                setOnEditorActionListener { v, actionId, event ->
                    hideSoftKeyboard()
                    false
                }
            }

            // 옵션2 체크박스
            checkBoxProductUpdateOption2.run {
                isChecked = dataOrigin.customOptionInfo["image_is_use"].toBoolean()

                if (isChecked) {
                    editinputlayoutProductUpdateOption2Price.hint =
                        dataOrigin.customOptionInfo["image_fee"]
                }
            }

            // 옵션2 가격입력
            editinputlayoutProductUpdateOption2Price.run {
                setOnEditorActionListener { v, actionId, event ->
                    hideSoftKeyboard()
                    false
                }
            }

            // 상품 상세정보
            editinputlayoutProductUpdateDetail.run {
                hint = dataOrigin.detail

                setOnEditorActionListener { v, actionId, event ->
                    hideSoftKeyboard()
                    false
                }
            }

            // 사진추가 버튼
            buttonProductUpdateAddPicture.setOnClickListener {
                hideSoftKeyboard()

                requestPermissions(permissionList, 0)
                callbackActionGranted = {
                    // 사진을 추가할 공간이 있는 지 확인
                    if (6 <= pictureUriList.size) {
                        Snackbar.make(it, "최대 6장의 사진만 추가할 수 있습니다", Toast.LENGTH_SHORT).show()
                    } else {
                        val newIntent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        newIntent.type = "image/*"
                        val mimeType = arrayOf("image/*")
                        newIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
                        albumActivityLauncherForPictures.launch(newIntent)
                    }
                }
                callbackActionDenide = {
                    Snackbar.make(it, "권한을 허용하세요", Toast.LENGTH_SHORT).show()
                }
            }

            // 사진 테이블 6개
            val pictureIncludeList = arrayListOf<ItemProductAddAddPictureBinding>()
            pictureIncludeList.add(includeProductUpdateAddPictureBox1)
            pictureIncludeList.add(includeProductUpdateAddPictureBox2)
            pictureIncludeList.add(includeProductUpdateAddPictureBox3)
            pictureIncludeList.add(includeProductUpdateAddPictureBox4)
            pictureIncludeList.add(includeProductUpdateAddPictureBox5)
            pictureIncludeList.add(includeProductUpdateAddPictureBox6)

            for (i in 0 until pictureIncludeList.size) {
                pictureIncludeList[i].buttonX.setOnClickListener {
                    if (i < pictureUriList.size) {
                        pictureUriList.removeAt(i)

                        if (i == pictureCheckIndex) {
                            pictureCheckIndex = -1
                        } else if (i < pictureCheckIndex) {
                            pictureCheckIndex -= 1
                        }
                    }
                    resetPictureView()
                }

                // 체크박스 동작
                pictureIncludeList[i].buttonCheckBox.setOnClickListener {
                    it.isSelected = !it.isSelected
                    if (it.isSelected) {
                        if (0 <= pictureCheckIndex) {
                            pictureIncludeList[pictureCheckIndex].buttonCheckBox.isSelected = false
                        }
                        pictureCheckIndex = i
                    }
                }
            }

            // 도면추가 버튼
            buttonProductUpdateAddPlan.setOnClickListener {
                hideSoftKeyboard()

                requestPermissions(permissionList, 0)
                // 권한 확인 후 액션
                callbackActionGranted = {
                    // 도면을 추가할 공간이 있는 지 확인
                    if (4 <= planUriList.size) {
                        Snackbar.make(it, "최대 4장의 도면만 추가할 수 있습니다", Toast.LENGTH_SHORT).show()
                    } else {
                        val newIntent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        newIntent.type = "image/*"
                        val mimeType = arrayOf("image/*")
                        newIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
                        albumActivityLauncherForPlans.launch(newIntent)
                    }
                }
                callbackActionDenide = {
                    Snackbar.make(it, "권한을 허용하세요", Toast.LENGTH_SHORT).show()
                }
            }

            // 도면 4개
            val planIncludeList = arrayListOf<ItemProductAddAddPlanBinding>()
            planIncludeList.add(includeProductUpdateAddPlanBox1)
            planIncludeList.add(includeProductUpdateAddPlanBox2)
            planIncludeList.add(includeProductUpdateAddPlanBox3)
            planIncludeList.add(includeProductUpdateAddPlanBox4)

            for (i in 0 until planIncludeList.size) {
                planIncludeList[i].buttonX.setOnClickListener {
                    if (i < planUriList.size) {
                        planUriList.removeAt(i)
                    }
                    resetPlanView()
                }
            }
        }
    }

    // 사진,도면 추가를 위한 런쳐 셋팅
    fun setAlbumActivityLaunchers() {

        // 사진 추가
        albumActivityLauncherForPictures =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it?.resultCode != Activity.RESULT_OK) {
                    return@registerForActivityResult
                }
                val clipData = (it.data?.clipData) ?: return@registerForActivityResult

                for (i in 0 until clipData.itemCount) {
                    if (6 <= pictureUriList.size) {
                        break
                    }

                    val uri = it.data!!.clipData!!.getItemAt(i).uri

                    // 버젼별 이미지 디코드
                    val bitmap = imageDecode(uri)

                    // 가져온 이미지가 있다면 저장하고 화면에 보여줌
                    if (bitmap != null) {
                        // 이미지 추가
                        pictureUriList.add(uri)
                    }
                }

                resetPictureView()
                Snackbar.make(
                    binding.root,
                    "사진을 추가했습니다",
                    Toast.LENGTH_SHORT
                )
                    .show()

            }

        // 도면 추가
        albumActivityLauncherForPlans =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it?.resultCode != Activity.RESULT_OK) {
                    return@registerForActivityResult
                }
                val clipData = (it.data?.clipData) ?: return@registerForActivityResult

                for (i in 0 until clipData.itemCount) {
                    if (4 <= planUriList.size) {
                        break
                    }

                    val uri = it.data!!.clipData!!.getItemAt(i).uri

                    // 버젼별 이미지 디코드
                    val bitmap = imageDecode(uri)

                    // 가져온 이미지가 있다면 저장하고 화면에 보여줌
                    if (bitmap != null) {
                        // 이미지 추가
                        planUriList.add(uri)
                    }
                }

                resetPlanView()
                Snackbar.make(
                    binding.root,
                    "도면을 추가했습니다",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
    }

    private fun imageDecode(uri: Uri): Bitmap? {
        // 버젼별 이미지 디코드
        var bitmap: Bitmap? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val source =
                ImageDecoder.createSource(mainActivity.contentResolver, uri)
            bitmap = ImageDecoder.decodeBitmap(source)
        } else {
            val cursor =
                mainActivity.contentResolver.query(uri, null, null, null, null)
            if (cursor != null) {
                cursor.moveToNext()

                val idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                val source = cursor.getString(idx)

                bitmap = BitmapFactory.decodeFile(source)
            }
        }

        return bitmap
    }

    private fun resetPictureView() {
        binding.run {
            // 사진 테이블 6개
            val pictureIncludeList = arrayListOf<ItemProductAddAddPictureBinding>()
            pictureIncludeList.add(includeProductUpdateAddPictureBox1)
            pictureIncludeList.add(includeProductUpdateAddPictureBox2)
            pictureIncludeList.add(includeProductUpdateAddPictureBox3)
            pictureIncludeList.add(includeProductUpdateAddPictureBox4)
            pictureIncludeList.add(includeProductUpdateAddPictureBox5)
            pictureIncludeList.add(includeProductUpdateAddPictureBox6)

            val width = pictureIncludeList[0].imageView.width
            for (i in 0 until pictureIncludeList.size) {
                if (i < pictureUriList.size) {
                    val bitmap = imageDecode(pictureUriList[i])!!
                    val crop = Bitmap.createScaledBitmap(bitmap, width, width, true)
                    pictureIncludeList[i].imageView.setImageBitmap(crop)
                    pictureIncludeList[i].buttonX.visibility = View.VISIBLE
                    pictureIncludeList[i].buttonCheckBox.visibility = View.VISIBLE
                } else {
                    pictureIncludeList[i].imageView.setImageDrawable(mainActivity.getDrawable(R.drawable.agijagi_logo_vector_square))
                    pictureIncludeList[i].buttonX.visibility = View.INVISIBLE
                    pictureIncludeList[i].buttonCheckBox.visibility = View.INVISIBLE
                }

                pictureIncludeList[i].buttonCheckBox.isSelected = (pictureCheckIndex == i)
            }
        }
    }

    private fun resetPlanView() {
        binding.run {
            // 도면 테이블 4개
            val planIncludeList = arrayListOf<ItemProductAddAddPlanBinding>()
            planIncludeList.add(includeProductUpdateAddPlanBox1)
            planIncludeList.add(includeProductUpdateAddPlanBox2)
            planIncludeList.add(includeProductUpdateAddPlanBox3)
            planIncludeList.add(includeProductUpdateAddPlanBox4)

            val width = planIncludeList[0].imageView.width
            for (i in 0 until planIncludeList.size) {
                if (i < planUriList.size) {
                    val bitmap = imageDecode(planUriList[i])!!
                    val crop = Bitmap.createScaledBitmap(bitmap, width, width, true)
                    planIncludeList[i].imageView.setImageBitmap(crop)
                    planIncludeList[i].root.visibility = View.VISIBLE
                } else {
                    planIncludeList[i].root.visibility = View.INVISIBLE
                }
            }
            textViewSubtitle3.text = "( ${planUriList.size.toInt()} / 4 )"
        }
    }


    // 유효성 검사
    // 하단 버튼을 클릭했을 경우의 유효성검사 메소드
    // 새 값이 있는 경우에만 수정함
    private fun setBottomButton() {
        binding.run {
            // 하단 버튼 클릭
            buttonProductUpdateOk.setOnClickListener {
                val name = editinputProductUpdateProductname.text.toString()
                if (name.isEmpty() || name == "" || name == " ") {
                } else {
                    dataOrigin.name = name
                }

                val price = editinputlayoutProductUpdateProductprice.text.toString()
                if (price.isEmpty() || price == "" || price == " ") {
                } else {
                    dataOrigin.price = price
                }

                if (categoryIdx != ListView.INVALID_POSITION) {
                    dataOrigin.category = (resources.getStringArray(R.array.product_add_category)
                        .toList())[categoryIdx]
                }

                // 옵션 추가
                val isOrdermade = categoryIdx == menuProductUpdateSelectCategory.adapter.count - 1
                if (isOrdermade) {
                    if (checkBoxProductUpdateOption1.isChecked) {
                        val op1 = editinputlayoutProductUpdateOption1Price.text.toString()
                        if (op1.isEmpty() || op1 == "" || op1 == " ") {
                        } else {
                            dataOrigin.customOptionInfo["lettering_fee"] = op1
                            dataOrigin.customOptionInfo["lettering_is_use"] = "true"
                        }
                    } else {
                        dataOrigin.customOptionInfo["lettering_fee"] = ""
                        dataOrigin.customOptionInfo["lettering_is_use"] = "false"
                    }

                    if (checkBoxProductUpdateOption2.isChecked) {
                        val op2 = editinputlayoutProductUpdateOption2Price.text.toString()
                        if (op2.isEmpty() || op2 == "" || op2 == " ") {
                        } else {
                            dataOrigin.customOptionInfo["image_fee"] = op2
                            dataOrigin.customOptionInfo["image_is_use"] = "true"
                        }
                    } else {
                        dataOrigin.customOptionInfo["image_fee"] = ""
                        dataOrigin.customOptionInfo["image_is_use"] = "false"
                    }
                }

                // 그 외 데이터
                val category =
                    menuProductUpdateSelectCategory.adapter.getItem(categoryIdx).toString()
                dataOrigin.category = category

                val planUriString = ArrayList<String>()
                planUriList.forEach { uri -> planUriString.add(uri.toString()) }
                dataOrigin.floorPlan = planUriString

                val pictureUriString = ArrayList<String>()
                pictureUriList.forEach { uri -> pictureUriString.add(uri.toString()) }
                dataOrigin.image = pictureUriString

                val thumbnailPictureUriString =
                    if (0 <= pictureCheckIndex && pictureCheckIndex < pictureUriString.size) pictureUriString[pictureCheckIndex]
                    else ""
                dataOrigin.thumbnail_image = thumbnailPictureUriString

                val detail = editinputlayoutProductUpdateDetail.text.toString()
                if (detail.isEmpty() || detail == "" || detail == " ") {
                } else {
                    dataOrigin.detail = detail
                }

                // 번들 생성, 전달
                val bundle = bundleOf("productData" to dataOrigin)
                findNavController().navigate(
                    R.id.action_productUpdateFragment_to_productUpdateDetailPreviewFragment,
                    bundle
                )
            }
        }
    }

    private fun hideSoftKeyboard() {
        if (mainActivity.currentFocus != null) {
            // currentFocus : 현재 포커스를 가지고 있는 View를 지칭할 수 있다.
            imm.hideSoftInputFromWindow(mainActivity.currentFocus!!.windowToken, 0)
            // 포커스를 해제한다.
            mainActivity.currentFocus!!.clearFocus()
        }
    }

    // 수정하려는 상품 데이터 가져오기
    private fun getDataOrigin(): ProductModel {
        val bundle = arguments
        val data = bundle?.getParcelable<ProductModel>("pruductData")
        if (data == null) {
            Snackbar.make(binding.root, "번들 데이터가 없습니다!!", Snackbar.LENGTH_SHORT).show()
        }

        return data!!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
