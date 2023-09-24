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
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
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

    // 사진, 도면 런쳐
    lateinit var albumActivityLauncherForPictures: ActivityResultLauncher<Intent>
    private var pictureCheckIndex: Int = -1
    lateinit var albumActivityLauncherForPlans: ActivityResultLauncher<Intent>

    // 수정할 판매글의 데이터
    private lateinit var dataOrigin: ProductModel

    // 이미지 가져올 DB
    private val storageRef = Firebase.storage.reference

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

        setToolbarItemAction()
        // 이미지 가져오기 런쳐 등록
        setAlbumActivityLaunchers()
        // 입력 UI 동작 등록
        setUiFunction()
        // 하단 버튼 동작 등록
        setBottomButton()

        // 수정할 데이터 가져오기
        dataOrigin = getDataOrigin()

        dataOrigin.run {
            // 썸네일 체크를 위한 인덱스 검색
            if (thumbnail_image != "") {
                pictureCheckIndex = image.indexOf(thumbnail_image)
                if (pictureCheckIndex == -1)
                    thumbnail_image = ""
            }
        }

        setUiInit()

        Handler(Looper.getMainLooper()).postDelayed({
            // 이미지 뷰 초기 설정
            resetPictureView()
            resetPlanView()
        }, 10)
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

    private fun setUiInit() {
        binding.run {
            val data = dataOrigin

            // 상품명
            if (data.name != "") {
                editinputProductUpdateProductname.hint = data.name
            }
            // 가격
            if (data.price != "") {
                editinputlayoutProductUpdateProductprice.hint = data.price
            }
            // 카테고리 선택
            if (data.category != "") {
                menuProductUpdateSelectCategory.setText(data.category, false)
            }

            // 주문제작 가능 여부 선택
            val ordermadeItems =
                resources.getStringArray(R.array.product_dropdown_select_isordermade)
            val ordermadeStr = if (data.isCustom) ordermadeItems[0] else ordermadeItems[1]
            menuProductUpdateSelectOrdermade.setText(ordermadeStr, false)
            if (data.isCustom) {
                layoutProductUpdateOption.visibility = View.VISIBLE
                layoutProductUpdateAddPlan.visibility = View.VISIBLE

                // 옵션1 체크박스, 가격
                val op1 = data.customOptionInfo["lettering_is_use"].toBoolean()
                checkBoxProductUpdateOption1.isChecked = op1
                editinputlayoutProductUpdateOption1Price.setText(if (op1) data.customOptionInfo["lettering_fee"] else "")
                // 옵션2 체크박스, 가격
                val op2 = data.customOptionInfo["image_is_use"].toBoolean()
                checkBoxProductUpdateOption2.isChecked = op2
                editinputlayoutProductUpdateOption2Price.setText(if (op2) data.customOptionInfo["image_fee"] else "")
            } else {
                layoutProductUpdateOption.visibility = View.GONE
                layoutProductUpdateAddPlan.visibility = View.GONE
            }

            // 상품 상세정보
            if (data.detail != "") {
                editinputlayoutProductUpdateDetail.hint = data.detail
            }
        }
    }

    private fun setUiFunction() {
        // 동작
        binding.run {
            // 상품명
            editinputProductUpdateProductname.run {
                setOnEditorActionListener { textView, i, keyEvent ->
                    hideSoftKeyboard()
                    false
                }
            }

            // 가격
            editinputlayoutProductUpdateProductprice.run {
                setOnEditorActionListener { textView, i, keyEvent ->
                    hideSoftKeyboard()
                    false
                }
            }

            // 카테고리 선택 // Plate, Cup, Bowl
            menuProductUpdateSelectCategory.run {
                setOnClickListener {
                    if (text.isNotEmpty())
                        (this.adapter as ArrayAdapter<String>?)?.filter?.filter(null)
                }
                setOnItemClickListener { adapterView, view, i, l ->
                    dataOrigin.category = adapter.getItem(i).toString()
                    hideSoftKeyboard()
                }
            }
            inputlayoutProductUpdateSelectCategory.setEndIconOnClickListener {
                menuProductUpdateSelectCategory.run {
                    if (text.isNotEmpty())
                        (adapter as ArrayAdapter<String>?)?.filter?.filter(
                            null
                        )
                    showDropDown()
                }
            }

            // 주문제작 가능 여부 선택 // 가능, 불가능
            menuProductUpdateSelectOrdermade.run {
                setOnClickListener {
                    if (text.isNotEmpty())
                        (this.adapter as ArrayAdapter<String>?)?.filter?.filter(null)
                }
                setOnItemClickListener { adapterView, view, i, l ->
                    hideSoftKeyboard()

                    val ordermadeItems =
                        resources.getStringArray(R.array.product_dropdown_select_isordermade)

                    when (adapter.getItem(i).toString()) {
                        ordermadeItems[0] -> { // 주문 제작 가능
                            dataOrigin.isCustom = true
                            layoutProductUpdateOption.visibility = View.VISIBLE
                            layoutProductUpdateAddPlan.visibility = View.VISIBLE
                        }

                        ordermadeItems[1] -> { // 주문 제작 불가능
                            dataOrigin.isCustom = false
                            layoutProductUpdateOption.visibility = View.GONE
                            layoutProductUpdateAddPlan.visibility = View.GONE
                        }
                    }
                }
            }
            inputlayoutProductUpdateSelectOrdermade.setOnClickListener {
                menuProductUpdateSelectOrdermade.run {
                    if (text.isNotEmpty())
                        (adapter as ArrayAdapter<String>?)?.filter?.filter(
                            null
                        )
                    showDropDown()
                }
            }

            // 옵션1 체크박스
            checkBoxProductUpdateOption1.run {
                setOnCheckedChangeListener { compoundButton, b ->
                    dataOrigin.customOptionInfo["lettering_is_use"] = b.toString()
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
                setOnCheckedChangeListener { compoundButton, b ->
                    dataOrigin.customOptionInfo["image_is_use"] = b.toString()
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
                    if (6 <= dataOrigin.image.size) {
                        Snackbar.make(it, "최대 6장의 사진만 추가할 수 있습니다", Snackbar.LENGTH_SHORT).show()
                    } else {
                        val newIntent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        newIntent.type = "image/*"
                        val mimeType = arrayOf("image/*")
                        newIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
                        newIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                        albumActivityLauncherForPictures.launch(newIntent)
                    }
                }
                callbackActionDenide = {
                    Snackbar.make(it, "권한을 허용하세요", Snackbar.LENGTH_SHORT).show()
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
                // X버튼 동작
                pictureIncludeList[i].buttonX.setOnClickListener {
                    if (i < dataOrigin.image.size) {
                        dataOrigin.image.removeAt(i)

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
                    if (4 <= dataOrigin.floorPlan.size) {
                        Snackbar.make(it, "최대 4장의 도면만 추가할 수 있습니다", Snackbar.LENGTH_SHORT).show()
                    } else {
                        val newIntent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        newIntent.type = "image/*"
                        val mimeType = arrayOf("image/*")
                        newIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
                        newIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                        albumActivityLauncherForPlans.launch(newIntent)
                    }
                }
                callbackActionDenide = {
                    Snackbar.make(it, "권한을 허용하세요", Snackbar.LENGTH_SHORT).show()
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
                    if (i < dataOrigin.floorPlan.size) {
                        dataOrigin.floorPlan.removeAt(i)
                    }
                    resetPlanView()
                }
            }
        }
    }

    // 사진,도면 추가를 위한 런쳐 셋팅
    private fun setAlbumActivityLaunchers() {
        // 사진 추가
        albumActivityLauncherForPictures =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it?.resultCode != Activity.RESULT_OK) {
                    return@registerForActivityResult
                }
                val clipData = (it.data?.clipData) ?: return@registerForActivityResult

                for (i in 0 until clipData.itemCount) {
                    if (6 <= dataOrigin.image.size) {
                        break
                    }
                    val uri = it.data!!.clipData!!.getItemAt(i).uri

                    if (imageDecode(uri) != null) {
                        // 이미지 추가
                        dataOrigin.image.add(uri.toString())
                    }
                }

                resetPictureView()
                Snackbar.make(binding.root, "사진을 추가했습니다", Snackbar.LENGTH_SHORT).show()
            }

        // 도면 추가
        albumActivityLauncherForPlans =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it?.resultCode != Activity.RESULT_OK) {
                    return@registerForActivityResult
                }
                val clipData = (it.data?.clipData) ?: return@registerForActivityResult

                for (i in 0 until clipData.itemCount) {
                    if (4 <= dataOrigin.floorPlan.size) {
                        break
                    }
                    val uri = it.data!!.clipData!!.getItemAt(i).uri

                    if (imageDecode(uri) != null) {
                        dataOrigin.floorPlan.add(uri.toString())
                    }
                }

                resetPlanView()
                Snackbar.make(binding.root, "도면을 추가했습니다", Snackbar.LENGTH_SHORT).show()
            }
    }

    // 버젼별 이미지 디코드
    private fun imageDecode(uri: Uri): Bitmap? {
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

    private fun downloadProductImageUri(
        fileName: String,
        action_success: (Uri) -> Unit,
        action_fail: () -> Unit = {}
    ) {
        storageRef.child(fileName).downloadUrl.addOnCompleteListener {
            if (it.isSuccessful) {
                action_success(it.result)
            } else {
                Log.e("StorageException", it.exception.toString())
                action_fail()
            }
        }
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
                if (i < dataOrigin.image.size) {
                    if (dataOrigin.image[i].startsWith("productImage/")
                    ) {
                        downloadProductImageUri(dataOrigin.image[i], {
                            Glide.with(binding.root)
                                .load(it)
                                .placeholder(R.drawable.search_result_default_image)
                                .centerCrop()
                                .into(pictureIncludeList[i].imageView)
                        })
                    } else {
                        val bitmap = imageDecode(Uri.parse(dataOrigin.image[i]))!!
                        val crop = Bitmap.createScaledBitmap(bitmap, width, width, true)
                        pictureIncludeList[i].imageView.setImageBitmap(crop)
                    }

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
                if (i < dataOrigin.floorPlan.size) {
                    if (dataOrigin.floorPlan[i].startsWith("productFloorPlan/")
                    ) {
                        downloadProductImageUri(dataOrigin.image[i], {
                            Glide.with(binding.root)
                                .load(it)
                                .placeholder(R.drawable.search_result_default_image)
                                .centerCrop()
                                .into(planIncludeList[i].imageView)
                        })
                    } else {
                        val bitmap = imageDecode(Uri.parse(dataOrigin.floorPlan[i]))!!
                        val crop = Bitmap.createScaledBitmap(bitmap, width, width, true)
                        planIncludeList[i].imageView.setImageBitmap(crop)
                    }

                    planIncludeList[i].root.visibility = View.VISIBLE
                } else {
                    planIncludeList[i].root.visibility = View.INVISIBLE
                }
            }
            textViewSubtitle3.text = "( ${dataOrigin.floorPlan.size.toInt()} / 4 )"
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

                val detail = editinputlayoutProductUpdateDetail.text.toString()
                if (detail.isEmpty() || detail == "" || detail == " ") {
                } else {
                    dataOrigin.detail = detail
                }

                // 그 외 데이터
                if (dataOrigin.isCustom) {
                    if (dataOrigin.customOptionInfo["lettering_is_use"] == "true") {
                        val op1price = editinputlayoutProductUpdateOption1Price.text.toString()
                        if (op1price.isEmpty() || op1price == "" || op1price == " ") {
                        } else {
                            dataOrigin.customOptionInfo["lettering_fee"] = op1price
                        }
                    }
                    if (dataOrigin.customOptionInfo["image_is_use"] == "true") {
                        val op2price = editinputlayoutProductUpdateOption2Price.text.toString()
                        if (op2price.isEmpty() || op2price == "" || op2price == " ") {
                        } else {
                            dataOrigin.customOptionInfo["image_fee"] = op2price
                        }
                    }
                } else {
                    dataOrigin.customOptionInfo["lettering_is_use"] = "false"
                    dataOrigin.customOptionInfo["lettering_fee"] = ""
                    dataOrigin.customOptionInfo["image_is_use"] = "false"
                    dataOrigin.customOptionInfo["image_fee"] = ""
                }

                dataOrigin.thumbnail_image =
                    if (0 <= pictureCheckIndex && pictureCheckIndex < dataOrigin.image.size)
                        dataOrigin.image[pictureCheckIndex]
                    else ""


                // 번들 생성, 전달
                val bundle = Bundle()
                bundle.putParcelable("productData", dataOrigin)
                bundle.putString("prodId", getProductId())

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
        val data = bundle?.getParcelable<ProductModel>("productData")
        if (data == null) {
            Snackbar.make(binding.root, "번들 데이터가 없습니다!!", Snackbar.LENGTH_SHORT).show()
        }

        return data!!
    }

    private fun getProductId(): String {
        return arguments?.getString("prodId").toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
