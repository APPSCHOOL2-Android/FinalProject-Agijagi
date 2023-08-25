package likelion.project.agijagi.purchase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentPaymentBinding

class PaymentFragment : Fragment() {

    lateinit var fragmentPaymentBinding: FragmentPaymentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentPaymentBinding = FragmentPaymentBinding.inflate(inflater)


        fragmentPaymentBinding.run {
            editinputlayoutPaymentCategoryDetail.visibility = View.INVISIBLE

            buttonPaymentCategoryCreditcard.run {
                setOnClickListener {
                    selectButtonColor(buttonPaymentCategoryCreditcard)
                    dropdownmenuPaymentCategory.run {
                        hint = "카드사를 선택해주세요."
                        (editText as? MaterialAutoCompleteTextView)?.setSimpleItems(R.array.payment_card_category)
                    }
                    dropdownmenuPaymentCategoryDetail.run {
                        visibility = View.VISIBLE
                        hint = "할부기간"
                        (editText as? MaterialAutoCompleteTextView)?.setSimpleItems(R.array.payment_card_detail_category)
                    }
                    editinputlayoutPaymentCategoryDetail.visibility = View.INVISIBLE
                }
            }

            buttonPaymentCategorySimplepay.run {
                setOnClickListener {
                    selectButtonColor(buttonPaymentCategorySimplepay)
                    dropdownmenuPaymentCategory.run {
                        hint = "결제수단을 선택해주세요."
                        (editText as? MaterialAutoCompleteTextView)?.setSimpleItems(R.array.payment_simple_category)
                    }
                    dropdownmenuPaymentCategoryDetail.visibility = View.INVISIBLE
                    editinputlayoutPaymentCategoryDetail.visibility = View.INVISIBLE
                }
            }

            buttonPaymentCategoryMobile.run {
                setOnClickListener {
                    selectButtonColor(buttonPaymentCategoryMobile)
                    dropdownmenuPaymentCategory.run {
                        hint = "통신사를 선택해주세요."
                        (editText as? MaterialAutoCompleteTextView)?.setSimpleItems(R.array.payment_phone_category)
                    }
                    dropdownmenuPaymentCategoryDetail.visibility = View.INVISIBLE
                    editinputlayoutPaymentCategoryDetail.run {
                        visibility = View.VISIBLE
                        hint = "전화번호를 입력하세요."
                    }
                }
            }

            buttonPaymentCategoryBank.run {
                setOnClickListener {
                    selectButtonColor(buttonPaymentCategoryBank)
                    dropdownmenuPaymentCategory.run {
                        hint = "은행을 선택해주세요."
                        (editText as? MaterialAutoCompleteTextView)?.setSimpleItems(R.array.payment_bank_transfer_category)
                    }
                    dropdownmenuPaymentCategoryDetail.visibility = View.INVISIBLE
                    editinputlayoutPaymentCategoryDetail.run {
                        visibility = View.VISIBLE
                        hint = "계좌번호를 입력하세요."
                    }
                }
            }
        }

        return fragmentPaymentBinding.root
    }

    // 버튼 누를시 글자색, 배경 변경
    private fun selectButtonColor(selectedButton: Button) {
        val buttons = arrayOf(
            fragmentPaymentBinding.buttonPaymentCategoryCreditcard,
            fragmentPaymentBinding.buttonPaymentCategorySimplepay,
            fragmentPaymentBinding.buttonPaymentCategoryMobile,
            fragmentPaymentBinding.buttonPaymentCategoryBank
        )

        for (button in buttons) {
            if (button == selectedButton) {
                button.setTextColor(resources.getColor(R.color.jagi_hint_color)) // 선택된 버튼 스타일 변경
                button.setBackgroundResource(R.drawable.narrow_box_rounded_button_solid)
            } else {
                button.setTextColor(resources.getColor(R.color.jagi_brown))
                button.setBackgroundResource(R.drawable.narrow_box_rounded_button_stroke)
            }
        }
    }


}