package likelion.project.agijagi.purchase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentPaymentBinding
import likelion.project.agijagi.model.OrderModel
import likelion.project.agijagi.model.ProdInfo
import likelion.project.agijagi.model.ProductModel
import likelion.project.agijagi.sellermypage.model.OrderManagementModel

class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    lateinit var prodInfo: ProdInfo
    lateinit var order: OrderModel

    val db = Firebase.firestore
    private val storageRef = Firebase.storage.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setShippingChangeButton()

        //todo 이전 페이지에서 정보를 받아 결제 버튼을 누르면 정보를 서버에 올려야 함

        // customoption 페이지에서 데이터 가져오기
        if (arguments?.getParcelable<ProdInfo>("prodInfo") != null) {
            prodInfo = arguments?.getParcelable("prodInfo")!!
        }


        binding.run {
            toolbarPayment.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            editinputlayoutPaymentCategoryDetail.visibility = View.INVISIBLE

            buttonPaymentCategoryCreditcard.run {
                setOnClickListener {
                    selectButtonColor(buttonPaymentCategoryCreditcard)
                    dropdownmenuPaymentCategory.run {
                        (editText as? MaterialAutoCompleteTextView)?.setSimpleItems(R.array.payment_card_category)
                    }
                    dropdownTextViewPaymentCategory.run {
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
                val orderId = MainActivity.getMilliSec()
                uploadOrderFloorPlans(orderId)

                it.findNavController()
                    .navigate(R.id.action_paymentFragment_to_purchaseCompleteFragment)
            }
        }
    }

    private fun uploadOrderFloorPlans(orderId: String) {
        val productFloorPlanFileNameList = mutableMapOf<String, String?>()
        prodInfo.diagram.forEach {
            if (it.value != null) {
                val orderFloorPlanFileName =
                    "orderFloorPlan/$orderId/${MainActivity.getMilliSec()}.jpg"
                productFloorPlanFileNameList[it.key] = orderFloorPlanFileName
                storageRef.child(orderFloorPlanFileName).putFile(it.value!!.toUri())
                    .addOnSuccessListener {
                        registerOrderData(orderId)
                    }
            } else {
                productFloorPlanFileNameList[it.key] = null
            }
        }
        prodInfo.diagram = productFloorPlanFileNameList as HashMap<String, String?>
    }

    private fun registerOrderData(orderId: String) {
        val shippingAddress = mutableMapOf<String, String>()

        val orderMap = hashMapOf(
            "orderId" to id,
            "date" to id,
            "deliveryFee" to "9000",
            "orderNum" to id,
            "shippingAddress" to shippingAddress,
            "state" to "",
            "totalPrice" to "150000",
            "buyerId" to ""
        )

        val prodInfo = hashMapOf(
            "prodInfoId" to prodInfo.prodInfoId,
            "count" to prodInfo.count,
            "option" to prodInfo.option,
            "price" to prodInfo.price,
            "diagram" to prodInfo.diagram,
            "customWord" to prodInfo.customWord,
            "customLocation" to prodInfo.customLocation
        )

        db.collection("order").document(orderId).set(orderMap)
        db.collection("order").document(orderId).collection("prod_Info").document(orderId)
            .set(prodInfo)
        db.collection("seller").document()
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

    // 결제 상태 enum 클래스
//    enum class

}