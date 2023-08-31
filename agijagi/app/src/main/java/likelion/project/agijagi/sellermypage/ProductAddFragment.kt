package likelion.project.agijagi.sellermypage

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentProductAddBinding
import likelion.project.agijagi.databinding.ItemProductAddAddPictureBinding
import likelion.project.agijagi.databinding.ItemProductAddAddPlanBinding
import kotlin.concurrent.thread


class ProductAddFragment : Fragment() {

    private var _binding: FragmentProductAddBinding? = null
    private val binding get() = _binding!!
    lateinit var mainActivity: MainActivity

    lateinit var imm: InputMethodManager

    var categoryIdx: Int = -1
    var ordermadeIdx: Int = -1

    // 앨범에서 사진추가
    private val permissionList = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_MEDIA_LOCATION
    )
    lateinit var albumActivityLauncherForPictures: ActivityResultLauncher<Intent>
    lateinit var albumActivityLauncherForPlans: ActivityResultLauncher<Intent>
    private val pictureList: ArrayList<Bitmap> = arrayListOf<Bitmap>()
    private val planList: ArrayList<Bitmap> = arrayListOf<Bitmap>()

    lateinit var callbackActionGranted: () -> Unit
    lateinit var callbackActionDenide: () -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProductAddBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        return fragmentProductAddBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        thread {
            SystemClock.sleep(500)
            // 키보드 해제
            imm = mainActivity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            hideSoftKeyboard()
            // 하단버튼 초기셋팅
            checkBottomButtonActive()
        }

        // 동작
        binding.run {
            // 초기화
            layoutProductAddOption.visibility = View.GONE
            layoutProductAddAddPlan.visibility = View.GONE

            categoryIdx = ListView.INVALID_POSITION
            ordermadeIdx = ListView.INVALID_POSITION


            // 상품명
            editinputProductAddProductname.run {
                setOnEditorActionListener { v, actionId, event ->
                    checkBottomButtonActive()
                    // 가격 입력으로 이동
                    editinputlayoutProductAddProductprice.requestFocus()
                    false
                }
            }

            // 가격
            editinputlayoutProductAddProductprice.run {
                setOnEditorActionListener { v, actionId, event ->
                    checkBottomButtonActive()
                    false
                }
            }

            // 카테고리 선택
            menuProductAddSelectCategory.run {
                onItemClickListener = OnItemClickListener { parent, v, position, id ->
                    categoryIdx = position
                    checkBottomButtonActive()
                    hideSoftKeyboard()
                }
            }

            // 주문제작 가능 여부 선택
            menuProductAddSelectOrdermade.run {
                onItemClickListener = OnItemClickListener { parent, v, position, id ->
                    ordermadeIdx = position
                    checkBottomButtonActive()
                    hideSoftKeyboard()

                    // R.array.product_add_category 참조
                    when (position) {
                        ProductAddSelectOrdermade.ORDER_POSSIBLE.idx -> {
                            layoutProductAddOption.visibility = View.VISIBLE
                            layoutProductAddAddPlan.visibility = View.VISIBLE

                            // 초기화
                            checkBoxProductAddOption1.isChecked = false
                            checkBoxProductAddOption2.isChecked = false
                            editinputlayoutProductAddOption1Price.setText("")
                            editinputlayoutProductAddOption2Price.setText("")
                        }

                        ProductAddSelectOrdermade.ORDER_IMPOSSIBLE.idx -> {
                            layoutProductAddOption.visibility = View.GONE
                            layoutProductAddAddPlan.visibility = View.GONE
                        }
                    }
                }
            }

            // 옵션1 가격입력
            editinputlayoutProductAddOption1Price.run {
                setOnEditorActionListener { v, actionId, event ->
                    checkBottomButtonActive()
                    hideSoftKeyboard()
                    false
                }
            }

            // 옵션2 가격입력
            editinputlayoutProductAddOption1Price.run {
                setOnEditorActionListener { v, actionId, event ->
                    checkBottomButtonActive()
                    hideSoftKeyboard()
                    false
                }
            }

            // 상품 상세정보
            editinputlayoutProductAddProductprice.run {
                setOnEditorActionListener { v, actionId, event ->
                    checkBottomButtonActive()
                    hideSoftKeyboard()
                    false
                }
            }


            // 사진추가 버튼
            buttonProductAddAddPicture.setOnClickListener {
                hideSoftKeyboard()

                requestPermissions(permissionList, 0)
                callbackActionGranted = {
                    // 사진을 추가할 공간이 있는 지 확인
                    if (6 <= pictureList.size) {
                        Snackbar.make(it, "최대 6장의 사진만 추가할 수 있습니다", Toast.LENGTH_SHORT).show()
                    } else {
                        val newIntent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        newIntent.setType("image/*")
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
            pictureIncludeList.add(includeProductAddAddPictureBox1)
            pictureIncludeList.add(includeProductAddAddPictureBox2)
            pictureIncludeList.add(includeProductAddAddPictureBox3)
            pictureIncludeList.add(includeProductAddAddPictureBox4)
            pictureIncludeList.add(includeProductAddAddPictureBox5)
            pictureIncludeList.add(includeProductAddAddPictureBox6)

            for (i in 0 until pictureIncludeList.size) {
                pictureIncludeList[i].buttonX.setOnClickListener {
                    if (i < pictureList.size) {
                        pictureList.removeAt(i)
                    }
                    resetPictureView()
                }
            }

            // 도면추가 버튼
            buttonProductAddAddPlan.setOnClickListener {
                hideSoftKeyboard()

                requestPermissions(permissionList, 0)
                // 권한 확인 후 액션
                callbackActionGranted = {
                    // 도면을 추가할 공간이 있는 지 확인
                    if (4 <= pictureList.size) {
                        Snackbar.make(it, "최대 4장의 도면만 추가할 수 있습니다", Toast.LENGTH_SHORT).show()
                    } else {
                        val newIntent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        newIntent.setType("image/*")
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
            planIncludeList.add(includeProductAddAddPlanBox1)
            planIncludeList.add(includeProductAddAddPlanBox2)
            planIncludeList.add(includeProductAddAddPlanBox3)
            planIncludeList.add(includeProductAddAddPlanBox4)

            for (i in 0 until planIncludeList.size) {
                planIncludeList[i].buttonX.setOnClickListener {
                    if (i < planList.size) {
                        planList.removeAt(i)
                    }
                    resetPlanView()
                }
            }

        }

        setAlbumActivityLaunchers()
        resetPictureView()
        resetPlanView()
        setBottomButton()

        return binding.root
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

    // 하단 버튼 활성 체크
    // 상품명, 가격, 카테고리, 주문제작가능여부 4가지만 확인해서 버튼을 활성화한다.
    // 유효성 검사는 별개로, 하단 버튼을 클릭 시 검사한다.
    private fun checkBottomButtonActive() {
        binding.run {
            var checker = true

            val name = editinputProductAddProductname.text.toString()
            val price = editinputlayoutProductAddProductprice.text.toString()
            if (name.isEmpty() || name == "" || name == " ") {
                checker = false
            } else if (price.isEmpty() || price == "" || price == " ") {
                checker = false
            } else if (categoryIdx == ListView.INVALID_POSITION) {
                checker = false
            } else if (ordermadeIdx == ListView.INVALID_POSITION) {
                checker = false
            }

            // 버튼 활성화
            if (checker) {
                buttonProductAddOk.setBackgroundResource(R.drawable.wide_box_bottom_active)
                val colorInt = mainActivity.resources.getColor(R.color.jagi_ivory, null)
                buttonProductAddOk.setTextColor(colorInt)
                buttonProductAddOk.isClickable = true
            } else {
                buttonProductAddOk.setBackgroundResource(R.drawable.wide_box_bottom_inactive)
                val colorInt = mainActivity.resources.getColor(R.color.jagi_ivory, null)
                buttonProductAddOk.setTextColor(colorInt)
                buttonProductAddOk.isClickable = false
            }
        }
    }


    // 사진,도면 추가를 위한 런쳐 셋팅
    fun setAlbumActivityLaunchers() {

        // 사진 추가
        albumActivityLauncherForPictures =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

                if (it?.resultCode != RESULT_OK) {
                    return@registerForActivityResult
                }
                val uri = (it.data?.data) ?: return@registerForActivityResult

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

                // 가져온 이미지가 있다면 저장하고 화면에 보여줌
                if (bitmap != null) {
                    // 이미지 추가
                    pictureList.add(bitmap!!)
                    resetPictureView()
                    Snackbar.make(
                        binding.root,
                        "사진을 추가했습니다",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            }

        // 도면 추가
        albumActivityLauncherForPlans =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

                if (it?.resultCode != RESULT_OK) {
                    return@registerForActivityResult
                }
                val uri = (it.data?.data) ?: return@registerForActivityResult

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

                // 가져온 이미지가 있다면 저장하고 화면에 보여줌
                if (bitmap != null) {
                    // 이미지 추가
                    planList.add(bitmap!!)
                    resetPlanView()
                    Snackbar.make(
                        binding.root,
                        "도면을 추가했습니다",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
    }

    private fun resetPictureView() {
        binding.run {
            // 사진 테이블 6개
            val pictureIncludeList = arrayListOf<ImageView>()
            pictureIncludeList.add(includeProductAddAddPictureBox1.imageView)
            pictureIncludeList.add(includeProductAddAddPictureBox2.imageView)
            pictureIncludeList.add(includeProductAddAddPictureBox3.imageView)
            pictureIncludeList.add(includeProductAddAddPictureBox4.imageView)
            pictureIncludeList.add(includeProductAddAddPictureBox5.imageView)
            pictureIncludeList.add(includeProductAddAddPictureBox6.imageView)

            val width = pictureIncludeList[0].width
            for (i in 0 until pictureIncludeList.size) {
                if (i < pictureList.size) {
                    val bitmap = Bitmap.createScaledBitmap(pictureList[i], width, width, true)
                    pictureIncludeList[i].setImageBitmap(bitmap)
                } else {
                    pictureIncludeList[i].setImageDrawable(mainActivity.getDrawable(R.drawable.agijagi_logo_vector_square))
                }
            }
        }
    }

    private fun resetPlanView() {
        binding.run {
            // 사진 테이블 6개
            val planIncludeList = arrayListOf<ItemProductAddAddPlanBinding>()
            planIncludeList.add(includeProductAddAddPlanBox1)
            planIncludeList.add(includeProductAddAddPlanBox2)
            planIncludeList.add(includeProductAddAddPlanBox3)
            planIncludeList.add(includeProductAddAddPlanBox4)

            val width = planIncludeList[0].imageView.width
            for (i in 0 until planIncludeList.size) {
                if (i < planList.size) {
                    val bitmap = Bitmap.createScaledBitmap(planList[i], width, width, true)
                    planIncludeList[i].imageView.setImageBitmap(bitmap)
                    planIncludeList[i].root.visibility = View.VISIBLE
                } else {
                    planIncludeList[i].root.visibility = View.INVISIBLE
                }
            }
            textViewSubtitle3.text = "( ${planList.size.toInt()} / 4 )"
        }
    }


    // 유효성 검사
    // 하단 버튼을 클릭했을 경우의 유효성검사 메소드
    // 길어서 함수로 뺌
    private fun setBottomButton() {
        binding.run {
            // 하단 버튼 클릭
            buttonProductAddOk.setOnClickListener {
                val name = editinputProductAddProductname.text.toString()
                if (name.isEmpty() || name == "" || name == " ") {
                    Snackbar.make(it, "상품명을 입력하세요", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val price = editinputlayoutProductAddProductprice.text.toString()
                if (price.isEmpty() || price == "" || price == " ") {
                    Snackbar.make(it, "상품가격을 입력하세요", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (categoryIdx == ListView.INVALID_POSITION) {
                    Snackbar.make(it, "카테고리를 선택하세요", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val options = ArrayList<ProductAddModel.OptionClass>()
                if (ordermadeIdx == ListView.INVALID_POSITION) {
                    Snackbar.make(it, "주문 제작 가능 여부를 선택하세요", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else if (ordermadeIdx == ProductAddSelectOrdermade.ORDER_POSSIBLE.idx) {
                    // 주문 제작 가능 옵션의 유효성 검사
                    var checkOption = false
                    if (checkBoxProductAddOption1.isChecked) {
                        val op1 = editinputlayoutProductAddOption1Price.text.toString()
                        if (op1.isEmpty() || op1 == "" || op1 == " ") {
                            Snackbar.make(it, "선택한 옵션의 가격을 입력하세요", Snackbar.LENGTH_SHORT)
                                .show()
                            return@setOnClickListener
                        } else {
                            checkOption = true
                            options.add(ProductAddModel().OptionClass("레터링", true, op1))
                        }
                    } else {
                        options.add(ProductAddModel().OptionClass("레터링", false, ""))
                    }
                    if (checkBoxProductAddOption2.isChecked) {
                        val op2 = editinputlayoutProductAddOption2Price.text.toString()
                        if (op2.isEmpty() || op2 == "" || op2 == " ") {
                            Snackbar.make(it, "선택한 옵션의 가격을 입력하세요", Snackbar.LENGTH_SHORT)
                                .show()
                            return@setOnClickListener
                        } else {
                            checkOption = true
                            options.add(ProductAddModel().OptionClass("그림", true, op2))
                        }
                    } else {
                        options.add(ProductAddModel().OptionClass("그림", false, ""))
                    }

                    if (!checkOption) {
                        Snackbar.make(it, "옵션을 하나 이상 추가하세요", Snackbar.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }

                // 데이터 클래스 생성
                val data = ProductAddModel().apply {
                    this.name = name
                    this.price = price
                    this.category = when (categoryIdx) {
                        ProductAddCategory.ALL.idx -> ProductAddCategory.ALL.str
                        ProductAddCategory.PLATE.idx -> ProductAddCategory.PLATE.str
                        ProductAddCategory.CUP.idx -> ProductAddCategory.CUP.str
                        ProductAddCategory.BOWL.idx -> ProductAddCategory.BOWL.str
                        ProductAddCategory.ORDER_MADE.idx -> ProductAddCategory.ORDER_MADE.str
                        else -> ProductAddCategory.ALL.str
                    }
                    this.ordermade = when (ordermadeIdx) {
                        ProductAddSelectOrdermade.ORDER_POSSIBLE.idx -> ProductAddSelectOrdermade.ORDER_POSSIBLE.str
                        ProductAddSelectOrdermade.ORDER_IMPOSSIBLE.idx -> ProductAddSelectOrdermade.ORDER_IMPOSSIBLE.str
                        else -> ProductAddSelectOrdermade.ORDER_IMPOSSIBLE.str
                    }
                    this.options = options

                    this.detail = editinputlayoutProductAddDetail.text.toString()
                }


                // 이미지 서버에 저장
                //pictureList
                //planList

                // 상품 이미지 저장 후 Uri 리스트 저장
                //data.pictures.add("Uri1")
                //data.pictures.add("Uri2")

                // 도면 이미지 저장 후 Uri 리스트 저장
                //data.plans.add("Uri1")
                //data.plans.add("Uri2")


                // 서버 저장
                // 디버그 찍어보기(개발용)
                data.debugData()
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

}

enum class ProductAddCategory(val idx: Int, val str: String) {
    ALL(0, "ALL"),
    PLATE(1, "Plate"),
    CUP(2, "Cup"),
    BOWL(3, "Bowl"),
    ORDER_MADE(4, "Order made"),
}

enum class ProductAddSelectOrdermade(val idx: Int, val str: String) {
    ORDER_POSSIBLE(0, "주문 제작 가능"),
    ORDER_IMPOSSIBLE(1, "주문 제작 불가능")
}
