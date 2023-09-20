package likelion.project.agijagi.sellermypage

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context.INPUT_METHOD_SERVICE
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentProductAddBinding
import likelion.project.agijagi.databinding.ItemProductAddAddPictureBinding
import likelion.project.agijagi.databinding.ItemProductAddAddPlanBinding
import likelion.project.agijagi.model.ProductModel
import kotlin.concurrent.thread

class ProductAddFragment : Fragment() {

    private var _binding: FragmentProductAddBinding? = null
    private val binding get() = _binding!!
    lateinit var mainActivity: MainActivity

    private lateinit var imm: InputMethodManager

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

    // 새 판매글의 데이터
    private lateinit var dataOrigin: ProductModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProductAddBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        thread {
            SystemClock.sleep(500)
            // 키보드 해제
            imm = mainActivity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            hideSoftKeyboard()
        }

        setToolbarItemAction()
        // 이미지 가져오기 런쳐 등록
        setAlbumActivityLaunchers()
        // 입력 UI 동작 등록
        setUiFunction()
        // 하단 버튼 동작 등록
        setBottomButton()

        // UI 초기화
        if (!this::dataOrigin.isInitialized) {
            dataOrigin = ProductModel()
        }

        setUiInit()
        // 하단버튼 초기셋팅
        checkBottomButtonActive()

        Handler(Looper.getMainLooper()).postDelayed({
            // 이미지 뷰 초기 설정
            resetPictureView()
            resetPlanView()
        }, 10)
    }

    private fun setToolbarItemAction() {
        binding.toolbarProductAdd.setNavigationOnClickListener {
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
                editinputProductAddProductname.setText(data.name)
            }
            // 가격
            if (data.name != "") {
                editinputlayoutProductAddProductprice.setText(data.name)
            }
            // 카테고리 선택
            if (data.category != "") {
                menuProductAddSelectCategory.setText(data.category, false)
            }

            // 옵션, 도면
            val itemList = resources.getStringArray(R.array.product_add_category).toList()
            if (data.category == itemList[itemList.size - 1]) {
                layoutProductAddOption.visibility = View.VISIBLE
                layoutProductAddAddPlan.visibility = View.VISIBLE

                // 옵션1 체크박스, 가격
                val op1 = data.customOptionInfo["lettering_is_use"].toBoolean()
                checkBoxProductAddOption1.isChecked = op1
                editinputlayoutProductAddOption1Price.setText(if (op1) data.customOptionInfo["lettering_fee"] else "")
                // 옵션2 체크박스, 가격
                val op2 = data.customOptionInfo["image_is_use"].toBoolean()
                checkBoxProductAddOption2.isChecked = op2
                editinputlayoutProductAddOption2Price.setText(if (op2) data.customOptionInfo["image_fee"] else "")
            } else {
                layoutProductAddOption.visibility = View.GONE
                layoutProductAddAddPlan.visibility = View.GONE
            }

            // 상품 상세정보
            if (data.detail != "") {
                editinputlayoutProductAddDetail.setText(data.detail)
            }
        }
    }

    private fun setUiFunction() {
        binding.run {
            // 상품명
            editinputProductAddProductname.run {
                setOnEditorActionListener { textView, i, keyEvent ->
                    hideSoftKeyboard()
                    checkBottomButtonActive()
                    false
                }
                setOnFocusChangeListener { view, b ->
                    if (!b) {
                        dataOrigin.name = text.toString()
                        hideSoftKeyboard()
                        checkBottomButtonActive()
                    }
                }
            }

            // 가격
            editinputlayoutProductAddProductprice.run {
                setOnEditorActionListener { textView, i, keyEvent ->
                    hideSoftKeyboard()
                    checkBottomButtonActive()
                    false
                }
                setOnFocusChangeListener { view, b ->
                    if (!b) {
                        dataOrigin.price = text.toString()
                        hideSoftKeyboard()
                        checkBottomButtonActive()
                    }
                }
            }

            // 카테고리 선택
            menuProductAddSelectCategory.run {
                onItemClickListener = OnItemClickListener { parent, v, position, id ->
                    dataOrigin.category = adapter.getItem(position).toString()

                    checkBottomButtonActive()
                    hideSoftKeyboard()

                    if (position == adapter.count - 1) {
                        // Order made == 주문제작 가능
                        layoutProductAddOption.visibility = View.VISIBLE
                        layoutProductAddAddPlan.visibility = View.VISIBLE

                        // 초기화
                        checkBoxProductAddOption1.isChecked = false
                        checkBoxProductAddOption2.isChecked = false
                        editinputlayoutProductAddOption1Price.text = null
                        editinputlayoutProductAddOption2Price.text = null
                    } else {
                        // Plate, Cup, Bowl
                        layoutProductAddOption.visibility = View.GONE
                        layoutProductAddAddPlan.visibility = View.GONE
                    }
                }
            }

            // 옵션1 체크박스
            checkBoxProductAddOption1.run {
                setOnCheckedChangeListener { compoundButton, b ->
                    dataOrigin.customOptionInfo["lettering_is_use"] = b.toString()
                }
            }

            // 옵션1 가격입력
            editinputlayoutProductAddOption1Price.run {
                setOnEditorActionListener { v, actionId, event ->
                    checkBottomButtonActive()
                    hideSoftKeyboard()
                    false
                }
                setOnFocusChangeListener { view, b ->
                    if (!b) {
                        dataOrigin.customOptionInfo["lettering_fee"] = text.toString()
                        hideSoftKeyboard()
                    }
                }
            }

            // 옵션2 체크박스
            checkBoxProductAddOption2.run {
                setOnCheckedChangeListener { compoundButton, b ->
                    dataOrigin.customOptionInfo["image_is_use"] = b.toString()
                }
            }
            // 옵션2 가격입력
            editinputlayoutProductAddOption2Price.run {
                setOnEditorActionListener { v, actionId, event ->
                    checkBottomButtonActive()
                    hideSoftKeyboard()
                    false
                }
                setOnFocusChangeListener { view, b ->
                    if (!b) {
                        dataOrigin.customOptionInfo["image_fee"] = text.toString()
                        hideSoftKeyboard()
                    }
                }
            }

            // 상품 상세정보
            editinputlayoutProductAddDetail.run {
                setOnEditorActionListener { v, actionId, event ->
                    checkBottomButtonActive()
                    hideSoftKeyboard()
                    false
                }
                setOnFocusChangeListener { view, b ->
                    if (!b) {
                        dataOrigin.detail = text.toString()
                        hideSoftKeyboard()
                    }
                }
            }

            // 사진추가 버튼
            buttonProductAddAddPicture.setOnClickListener {
                hideSoftKeyboard()

                requestPermissions(permissionList, 0)
                callbackActionGranted = {
                    // 사진을 추가할 공간이 있는 지 확인
                    if (6 <= dataOrigin.image.size) {
                        Snackbar.make(it, "최대 6장의 사진만 추가할 수 있습니다", Toast.LENGTH_SHORT).show()
                    } else {
                        val newIntent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        newIntent.setType("image/*")
                        val mimeType = arrayOf("image/*")
                        newIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
                        newIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                        albumActivityLauncherForPictures.launch(newIntent)
                    }
                }
                callbackActionDenide = {
                    Snackbar.make(it, "권한을 허용하세요", Toast.LENGTH_SHORT).show()
                }
            }

            // 사진 테이블 6개
            val pictureIncludeList = arrayListOf<ItemProductAddAddPictureBinding>()
            pictureIncludeList.add(includeProductAddAddPictureBox1)
            pictureIncludeList.add(includeProductAddAddPictureBox2)
            pictureIncludeList.add(includeProductAddAddPictureBox3)
            pictureIncludeList.add(includeProductAddAddPictureBox4)
            pictureIncludeList.add(includeProductAddAddPictureBox5)
            pictureIncludeList.add(includeProductAddAddPictureBox6)

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
            buttonProductAddAddPlan.setOnClickListener {
                hideSoftKeyboard()

                requestPermissions(permissionList, 0)
                // 권한 확인 후 액션
                callbackActionGranted = {
                    // 도면을 추가할 공간이 있는 지 확인
                    if (4 <= dataOrigin.floorPlan.size) {
                        Snackbar.make(it, "최대 4장의 도면만 추가할 수 있습니다", Toast.LENGTH_SHORT).show()
                    } else {
                        val newIntent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        newIntent.setType("image/*")
                        val mimeType = arrayOf("image/*")
                        newIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
                        newIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                        albumActivityLauncherForPlans.launch(newIntent)
                    }
                }
                callbackActionDenide = {
                    Snackbar.make(it, "권한을 허용하세요", Toast.LENGTH_SHORT).show()
                }
            }

            // 도면 4개
            val planIncludeList = arrayListOf<ItemProductAddAddPlanBinding>()
            planIncludeList.add(includeProductAddAddPlanBox1)
            planIncludeList.add(includeProductAddAddPlanBox2)
            planIncludeList.add(includeProductAddAddPlanBox3)
            planIncludeList.add(includeProductAddAddPlanBox4)

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


    // 하단 버튼 활성 체크
    // 상품명, 가격, 카테고리만 확인해서 버튼을 활성화한다.
    // 유효성 검사는 별개로, 하단 버튼을 클릭 시 검사한다.
    private fun checkBottomButtonActive() {
        binding.run {
            val data = dataOrigin

            val checker = !(data.name == "" || data.name == " " ||
                    data.price == "" || data.price == " " ||
                    data.category == "" || data.category == " ")

            // 버튼 활성화
            buttonProductAddOk.isSelected = checker
            buttonProductAddOk.isClickable = checker
        }
    }


    // 사진,도면 추가를 위한 런쳐 셋팅
    private fun setAlbumActivityLaunchers() {
        // 사진 추가
        albumActivityLauncherForPictures =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it?.resultCode != RESULT_OK) {
                    return@registerForActivityResult
                }
                val clipData = (it.data?.clipData) ?: return@registerForActivityResult

                for (i in 0 until clipData.itemCount) {
                    if (6 <= dataOrigin.image.size) {
                        break
                    }

                    val uri = it.data!!.clipData!!.getItemAt(i).uri

                    // 버젼별 이미지 디코드
                    val bitmap = imageDecode(uri)

                    // 가져온 이미지가 있다면 저장하고 화면에 보여줌
                    if (bitmap != null) {
                        // 이미지 추가
                        dataOrigin.image.add(uri.toString())
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
                if (it?.resultCode != RESULT_OK) {
                    return@registerForActivityResult
                }
                val clipData = (it.data?.clipData) ?: return@registerForActivityResult

                for (i in 0 until clipData.itemCount) {
                    if (4 <= dataOrigin.floorPlan.size) {
                        break
                    }

                    val uri = it.data!!.clipData!!.getItemAt(i).uri

                    // 버젼별 이미지 디코드
                    val bitmap = imageDecode(uri)

                    // 가져온 이미지가 있다면 저장하고 화면에 보여줌
                    if (bitmap != null) {
                        // 이미지 추가
                        dataOrigin.floorPlan.add(uri.toString())
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

        // 이미지 크기 제한
        bitmap!!.apply {
            if (1024 < width || 1024 < height) {
                Snackbar.make(binding.root, "이미지 사이즈가 너무 큽니다", Snackbar.LENGTH_SHORT).show()
                return null
            }
        }
        return bitmap
    }

    private fun resetPictureView() {
        binding.run {
            // 사진 테이블 6개
            val pictureIncludeList = arrayListOf<ItemProductAddAddPictureBinding>()
            pictureIncludeList.add(includeProductAddAddPictureBox1)
            pictureIncludeList.add(includeProductAddAddPictureBox2)
            pictureIncludeList.add(includeProductAddAddPictureBox3)
            pictureIncludeList.add(includeProductAddAddPictureBox4)
            pictureIncludeList.add(includeProductAddAddPictureBox5)
            pictureIncludeList.add(includeProductAddAddPictureBox6)

            val width = pictureIncludeList[0].imageView.width
            for (i in 0 until pictureIncludeList.size) {
                if (i < dataOrigin.image.size) {
                    val bitmap = imageDecode(Uri.parse(dataOrigin.image[i]))!!
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
            planIncludeList.add(includeProductAddAddPlanBox1)
            planIncludeList.add(includeProductAddAddPlanBox2)
            planIncludeList.add(includeProductAddAddPlanBox3)
            planIncludeList.add(includeProductAddAddPlanBox4)

            val width = planIncludeList[0].imageView.width
            for (i in 0 until planIncludeList.size) {
                if (i < dataOrigin.floorPlan.size) {
                    val bitmap = imageDecode(Uri.parse(dataOrigin.floorPlan[i]))!!
                    val crop = Bitmap.createScaledBitmap(bitmap, width, width, true)
                    planIncludeList[i].imageView.setImageBitmap(crop)
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
    // 길어서 함수로 뺌
    private fun setBottomButton() {
        binding.run {
            // 하단 버튼 클릭
            buttonProductAddOk.setOnClickListener {
                val data = dataOrigin
                data.run {
                    if (name == "" || name == " ") {
                        Snackbar.make(it, "상품명을 입력하세요", Snackbar.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    if (price == "" || price == " ") {
                        Snackbar.make(it, "상품가격을 입력하세요", Snackbar.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    if (category == "") {
                        Snackbar.make(it, "카테고리를 선택하세요", Snackbar.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    // 주문 제작 가능 옵션의 유효성 검사
                    if (isCustom) {
                        if (customOptionInfo["lettering_is_use"] == "true" && customOptionInfo["lettering_fee"] == "") {
                            Snackbar.make(it, "선택한 옵션의 가격을 입력하세요", Snackbar.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                        if (customOptionInfo["image_is_use"] == "true" && customOptionInfo["image_fee"] == "") {
                            Snackbar.make(it, "선택한 옵션의 가격을 입력하세요", Snackbar.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                        if (customOptionInfo["lettering_is_use"] == "false" && customOptionInfo["image_is_use"] == "false") {
                            Snackbar.make(it, "옵션을 하나 이상 추가하세요", Snackbar.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                    } else {
                        customOptionInfo["lettering_is_use"] = "false"
                        customOptionInfo["lettering_fee"] = ""
                        customOptionInfo["image_is_use"] = "false"
                        customOptionInfo["image_fee"] = ""
                    }
                }

                // 번들 생성, 전달
                val bundle = bundleOf("productData" to data)
                findNavController().navigate(
                    R.id.action_productAddFragment_to_productDetailPreviewFragment,
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}