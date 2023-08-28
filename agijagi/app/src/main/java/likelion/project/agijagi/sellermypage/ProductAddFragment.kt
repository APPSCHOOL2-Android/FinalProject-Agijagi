package likelion.project.agijagi.sellermypage

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentProductAddBinding
import kotlin.concurrent.thread


class ProductAddFragment : Fragment() {

    lateinit var fragmentProductAddBinding: FragmentProductAddBinding
    lateinit var mainActivity: MainActivity

    lateinit var imm: InputMethodManager

    var categoryIdx: Int = -1
    var ordermadeIdx: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentProductAddBinding = FragmentProductAddBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        thread {
            SystemClock.sleep(500)

            // 키보드 해제
            imm = mainActivity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            hideSoftKeyboard()

            // 하단버튼 초기셋팅
            checkBottomButtonActive()
        }

        // 동작
        fragmentProductAddBinding.run {
            // 초기화
            layoutProductAddOption.visibility = View.GONE
            layoutProductAddAddPlan.visibility = View.GONE

            categoryIdx = ListView.INVALID_POSITION
            ordermadeIdx = ListView.INVALID_POSITION

            // 사진추가 버튼
            buttonProductAddAddPicture.setOnClickListener {
                Snackbar.make(it, "사진을 추가했습니다", Toast.LENGTH_SHORT).show()
                checkBottomButtonActive()
                hideSoftKeyboard()
            }

            // 도면추가 버튼
            buttonProductAddAddPlan.setOnClickListener {
                Snackbar.make(it, "도면을 추가했습니다", Toast.LENGTH_SHORT).show()
                checkBottomButtonActive()
                hideSoftKeyboard()
            }

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
                        0 -> { // 주문 제작 가능
                            layoutProductAddOption.visibility = View.VISIBLE
                            layoutProductAddAddPlan.visibility = View.VISIBLE

                            // 초기화
                            checkBoxProductAddOption1.isChecked = false
                            checkBoxProductAddOption2.isChecked = false
                            editinputlayoutProductAddOption1Price.setText("")
                            editinputlayoutProductAddOption2Price.setText("")
                        }

                        1 -> { // 주문 제작 불가능
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

        }

        setBottomButton()

        return fragmentProductAddBinding.root
    }

    // 하단 버튼 활성 체크
    // 상품명, 가격, 카테고리, 주문제작가능여부 4가지만 확인해서 버튼을 활성화한다.
    // 유효성 검사는 별개로, 하단 버튼을 클릭 시 검사한다.
    private fun checkBottomButtonActive() {
        fragmentProductAddBinding.run {
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


    // 유효성 검사
    // 하단 버튼을 클릭했을 경우의 유효성검사 메소드
    // 길어서 함수로 뺌
    fun setBottomButton() {
        fragmentProductAddBinding.run {
            // 하단 버튼 클릭
            buttonProductAddOk.setOnClickListener {
                // 상품명
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

                if (ordermadeIdx == ListView.INVALID_POSITION) {
                    Snackbar.make(it, "주문 제작 가능 여부를 선택하세요", Snackbar.LENGTH_SHORT).show()

                    return@setOnClickListener
                }

                // 데이터 저장

            }
        }
    }

    fun hideSoftKeyboard() {
        if (mainActivity.currentFocus != null) {
            // currentFocus : 현재 포커스를 가지고 있는 View를 지칭할 수 있다.
            imm.hideSoftInputFromWindow(mainActivity.currentFocus!!.windowToken, 0)
            // 포커스를 해제한다.
            mainActivity.currentFocus!!.clearFocus()
        }
    }


}
