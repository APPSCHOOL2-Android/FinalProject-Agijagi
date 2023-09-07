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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentProductUpdateBinding
import likelion.project.agijagi.databinding.ItemProductAddAddPictureBinding
import likelion.project.agijagi.databinding.ItemProductAddAddPlanBinding
import kotlin.concurrent.thread

class ProductUpdateFragment : Fragment() {

    private var _binding: FragmentProductUpdateBinding? = null
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

    // 수정할 판매글의 데이터
    private lateinit var dataOrigin: ProductUpdateModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        // 원 게시글의 이미지 데이터 가져오기
        pictureList.clear()
        if (0 < dataOrigin.pictures.size) {
            for (i in 0 until dataOrigin.pictures.size) {
                val bitmap = imageDecode(Uri.parse(dataOrigin.pictures[i]))
                if (bitmap != null) {
                    pictureList.add(bitmap!!)
                }
            }
        }
        planList.clear()
        if (0 < dataOrigin.plans.size) {
            for (i in 0 until dataOrigin.plans.size) {
                val bitmap = imageDecode(Uri.parse(dataOrigin.plans[i]))
                if (bitmap != null) {
                    planList.add(bitmap!!)
                }
            }
        }

        // 동작
        binding.run {
            // 초기화
            layoutProductUpdateOption.visibility = View.GONE
            layoutProductUpdateAddPlan.visibility = View.GONE

            categoryIdx = ListView.INVALID_POSITION
            ordermadeIdx = ListView.INVALID_POSITION

            // 하단 버튼 활성화 고정
            buttonProductUpdateOk.setBackgroundResource(R.drawable.wide_box_bottom_active)
            val colorInt = mainActivity.resources.getColor(R.color.jagi_ivory, null)
            buttonProductUpdateOk.setTextColor(colorInt)
            buttonProductUpdateOk.isClickable = true


            // 상품명
            editinputProductUpdateProductname.run {
                hint = dataOrigin.name
                setOnEditorActionListener { v, actionId, event ->
                    // 가격 입력으로 이동
                    editinputlayoutProductUpdateProductprice.requestFocus()
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

            // 주문제작 가능 여부 선택
            menuProductUpdateSelectOrdermade.run {
                if (dataOrigin.ordermade != "") {
                    setText(dataOrigin.ordermade, false)

                    ordermadeIdx =
                        resources.getStringArray(R.array.product_add_select_ordermade).toList()
                            .indexOf(dataOrigin.ordermade)

                    if (dataOrigin.ordermade == ProductAddSelectOrdermade.ORDER_POSSIBLE.str) {
                        layoutProductUpdateOption.visibility = View.VISIBLE
                        layoutProductUpdateAddPlan.visibility = View.VISIBLE
                    }
                }

                onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
                    ordermadeIdx = position
                    hideSoftKeyboard()

                    // R.array.product_add_category 참조
                    when (position) {
                        ProductAddSelectOrdermade.ORDER_POSSIBLE.idx -> {
                            layoutProductUpdateOption.visibility = View.VISIBLE
                            layoutProductUpdateAddPlan.visibility = View.VISIBLE
                        }

                        ProductAddSelectOrdermade.ORDER_IMPOSSIBLE.idx -> {
                            layoutProductUpdateOption.visibility = View.GONE
                            layoutProductUpdateAddPlan.visibility = View.GONE
                        }
                    }
                }
            }

            // 옵션1 체크박스
            checkBoxProductUpdateOption1.run {
                isChecked = dataOrigin.options[0].isCheck

                if (isChecked) {
                    editinputlayoutProductUpdateOption1Price.hint = dataOrigin.options[0].opPrice
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
                isChecked = dataOrigin.options[1].isCheck

                if (isChecked) {
                    editinputlayoutProductUpdateOption2Price.hint = dataOrigin.options[1].opPrice
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
            pictureIncludeList.add(includeProductUpdateAddPictureBox1)
            pictureIncludeList.add(includeProductUpdateAddPictureBox2)
            pictureIncludeList.add(includeProductUpdateAddPictureBox3)
            pictureIncludeList.add(includeProductUpdateAddPictureBox4)
            pictureIncludeList.add(includeProductUpdateAddPictureBox5)
            pictureIncludeList.add(includeProductUpdateAddPictureBox6)

            for (i in 0 until pictureIncludeList.size) {
                pictureIncludeList[i].buttonX.setOnClickListener {
                    if (i < pictureList.size) {
                        pictureList.removeAt(i)
                    }
                    resetPictureView()
                }
            }

            // 도면추가 버튼
            buttonProductUpdateAddPlan.setOnClickListener {
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
            planIncludeList.add(includeProductUpdateAddPlanBox1)
            planIncludeList.add(includeProductUpdateAddPlanBox2)
            planIncludeList.add(includeProductUpdateAddPlanBox3)
            planIncludeList.add(includeProductUpdateAddPlanBox4)

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


    // 사진,도면 추가를 위한 런쳐 셋팅
    fun setAlbumActivityLaunchers() {

        // 사진 추가
        albumActivityLauncherForPictures =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

                if (it?.resultCode != Activity.RESULT_OK) {
                    return@registerForActivityResult
                }
                val uri = (it.data?.data) ?: return@registerForActivityResult

                // 버젼별 이미지 디코드
                val bitmap = imageDecode(uri)

                // 가져온 이미지가 있다면 저장하고 화면에 보여줌
                if (bitmap != null) {
                    // 이미지 추가
                    pictureList.add(bitmap!!)
                    resetPictureView()
                    Snackbar.make(
                        binding.root,
                        "사진을 추가했습니다",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

        // 도면 추가
        albumActivityLauncherForPlans =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

                if (it?.resultCode != Activity.RESULT_OK) {
                    return@registerForActivityResult
                }
                val uri = (it.data?.data) ?: return@registerForActivityResult

                // 버젼별 이미지 디코드
                val bitmap = imageDecode(uri)

                // 가져온 이미지가 있다면 저장하고 화면에 보여줌
                if (bitmap != null) {
                    // 이미지 추가
                    planList.add(bitmap!!)
                    resetPlanView()
                    Snackbar.make(
                        binding.root,
                        "도면을 추가했습니다",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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
            val pictureIncludeList = arrayListOf<ImageView>()
            pictureIncludeList.add(includeProductUpdateAddPictureBox1.imageView)
            pictureIncludeList.add(includeProductUpdateAddPictureBox2.imageView)
            pictureIncludeList.add(includeProductUpdateAddPictureBox3.imageView)
            pictureIncludeList.add(includeProductUpdateAddPictureBox4.imageView)
            pictureIncludeList.add(includeProductUpdateAddPictureBox5.imageView)
            pictureIncludeList.add(includeProductUpdateAddPictureBox6.imageView)

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
            planIncludeList.add(includeProductUpdateAddPlanBox1)
            planIncludeList.add(includeProductUpdateAddPlanBox2)
            planIncludeList.add(includeProductUpdateAddPlanBox3)
            planIncludeList.add(includeProductUpdateAddPlanBox4)

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

                if (ordermadeIdx != ListView.INVALID_POSITION) {
                    dataOrigin.ordermade =
                        (resources.getStringArray(R.array.product_add_select_ordermade)
                            .toList())[ordermadeIdx]

                    val op1 = ProductUpdateModel().OptionClass(
                        "레터링",
                        checkBoxProductUpdateOption1.isChecked,
                        editinputlayoutProductUpdateOption1Price.text.toString()
                    )
                    dataOrigin.options[0] = op1
                    val op2 = ProductUpdateModel().OptionClass(
                        "그림",
                        checkBoxProductUpdateOption2.isChecked,
                        editinputlayoutProductUpdateOption2Price.text.toString()
                    )
                    dataOrigin.options[1] = op2
                }

                val detail = editinputlayoutProductUpdateDetail.text.toString()
                if (detail.isEmpty() || detail == "" || detail == " ") {
                } else {
                    dataOrigin.detail = detail
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
                dataOrigin.debugData()

                findNavController().navigate(R.id.action_productUpdateFragment_to_productDetailPreviewFragment)
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
    private fun getDataOrigin(): ProductUpdateModel {
        // 개발용 테스트 데이터
        val data = ProductUpdateModel().apply {
            name = "상품의 이름"
            price = "1000"
            category = "Plate"
            ordermade = "주문 제작 가능"
            options = arrayListOf(
                this.OptionClass("레터링", true, "2000"),
                this.OptionClass("그림", false, "1000")
            )
            detail = "상품의 설명입니다"
            pictures = arrayListOf() //("Uri1", "Uri2") // Uri
            plans = arrayListOf() //("Uri1", "Uri2") // Uri
            //date = "" // date 수정 불가
        }

        return data
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
