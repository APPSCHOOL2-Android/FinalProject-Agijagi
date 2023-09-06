package likelion.project.agijagi.purchase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentPaymentBinding

class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setShippingChangeButton()

        binding.run {
            editinputlayoutPaymentCategoryDetail.visibility = View.INVISIBLE

            buttonPaymentCategoryCreditcard.run {
                setOnClickListener {
                    selectButtonColor(buttonPaymentCategoryCreditcard)
                    dropdownmenuPaymentCategory.run {
                        (editText as? MaterialAutoCompleteTextView)?.setSimpleItems(R.array.payment_card_category)
                    }
                    dropdownTextViewPaymentCategory.run{
                        setText("")
                        hint = "카드사를 선택해주세요."
                    }
                    dropdownmenuPaymentCategoryDetail.run {
                        visibility = View.VISIBLE
                        (editText as? MaterialAutoCompleteTextView)?.setSimpleItems(R.array.payment_card_detail_category)
                    }
                    dropdownTextViewPaymentCategoryDetail.run {
                        setText("")
                        hint = "할부기간"
                    }



                    editinputlayoutPaymentCategoryDetail.visibility = View.INVISIBLE
                }
            }

            buttonPaymentCategorySimplepay.run {
                setOnClickListener {
                    selectButtonColor(buttonPaymentCategorySimplepay)
                    dropdownmenuPaymentCategory.run {
                        (editText as? MaterialAutoCompleteTextView)?.setSimpleItems(R.array.payment_simple_category)
                    }
                    dropdownTextViewPaymentCategory.run {
                        setText("")
                        hint = "결제수단을 선택해주세요."
                    }
                    dropdownTextViewPaymentCategoryDetail.setText("")
                    dropdownmenuPaymentCategoryDetail.visibility = View.INVISIBLE
                    editinputlayoutPaymentCategoryDetail.visibility = View.INVISIBLE
                }
            }

            buttonPaymentCategoryMobile.run {
                setOnClickListener {
                    selectButtonColor(buttonPaymentCategoryMobile)
                    dropdownmenuPaymentCategory.run {
                        (editText as? MaterialAutoCompleteTextView)?.setSimpleItems(R.array.payment_phone_category)
                    }
                    dropdownTextViewPaymentCategory.run {
                        setText("")
                        hint = "통신사를 선택해주세요."
                    }
                    dropdownTextViewPaymentCategoryDetail.setText("")
                    dropdownmenuPaymentCategoryDetail.visibility = View.INVISIBLE
                    editinputlayoutPaymentCategoryDetail.visibility = View.VISIBLE
                    editinputPaymentCategoryDetail.hint = "전화번호를 입력하세요."
                }
            }

            buttonPaymentCategoryBank.run {
                setOnClickListener {
                    selectButtonColor(buttonPaymentCategoryBank)
                    dropdownmenuPaymentCategory.run {
                        (editText as? MaterialAutoCompleteTextView)?.setSimpleItems(R.array.payment_bank_transfer_category)
                    }
                    dropdownTextViewPaymentCategory.run {
                        setText("")
                        hint = "은행을 선택해주세요."
                    }
                    dropdownTextViewPaymentCategoryDetail.setText("")
                    dropdownmenuPaymentCategoryDetail.visibility = View.INVISIBLE
                    editinputlayoutPaymentCategoryDetail.visibility = View.VISIBLE
                    editinputPaymentCategoryDetail.hint = "계좌번호를 입력하세요."
                }
            }

            buttonPaymentPayment.setOnClickListener {
                it.findNavController().navigate(R.id.action_paymentFragment_to_purchaseCompleteFragment)
            }
        }
    }

    // 버튼 누를시 글자색, 배경 변경
    private fun selectButtonColor(selectedButton: Button) {
        val buttons = arrayOf(
            binding.buttonPaymentCategoryCreditcard,
            binding.buttonPaymentCategorySimplepay,
            binding.buttonPaymentCategoryMobile,
            binding.buttonPaymentCategoryBank
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

    private fun setShippingChangeButton() {
        binding.buttonPaymentShippingChange.setOnClickListener {
            findNavController().navigate(R.id.action_paymentFragment_to_shippingManagementFragment)
        }
    }

}