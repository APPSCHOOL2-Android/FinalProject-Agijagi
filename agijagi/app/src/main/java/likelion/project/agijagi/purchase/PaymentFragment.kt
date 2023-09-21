package likelion.project.agijagi.purchase

import android.os.Bundle
import android.util.Log
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
import likelion.project.agijagi.buyermypage.repository.ShippingManagementRepository
import likelion.project.agijagi.databinding.FragmentPaymentBinding
import likelion.project.agijagi.model.OrderModel
import likelion.project.agijagi.model.ProdInfo
import likelion.project.agijagi.model.UserModel
import java.text.DecimalFormat

class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    lateinit var prodInfo: ProdInfo
    var order: OrderModel = OrderModel("",null,"","","", mutableMapOf(),"","","")
    lateinit var changeId: String
    val dec = DecimalFormat("#,###")
    var shippingViewState = false

    val db = Firebase.firestore
    private val storageRef = Firebase.storage.reference
    private val shippingManagementRepository = ShippingManagementRepository()

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
            setPriceTextView()
        }

        // 배송지 변경시 가져온 배송지 ID
        if (findNavController().currentBackStackEntry?.savedStateHandle?.get<String>("changeShippingId") != null) {
            changeId =
                findNavController().currentBackStackEntry?.savedStateHandle?.get<String>("changeShippingId")
                    .toString()
            shippingViewState = true
        }

        getShippingAddress()

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
                val orderDate = MainActivity.getMilliSec()
                if (prodInfo.option == "Image") {
                    uploadOrderFloorPlans(orderDate)
                }
                if (prodInfo.option == "Lettering") {
                    registerOrderData(orderDate)
                }
//                it.findNavController()
//                    .navigate(R.id.action_paymentFragment_to_purchaseCompleteFragment, bundle)
            }
        }
    }

    // 데이터 로딩 shimmer 라이브러리 사용
    private fun showSampleData(isLoading: Boolean) {
        if (isLoading) {
            binding.shimmerPaymentShippingAddress.startShimmer()
            binding.shimmerPaymentShippingAddress.visibility = View.VISIBLE
            binding.constraintPaymentShippingAddress.visibility = View.GONE
        } else {
            binding.shimmerPaymentShippingAddress.stopShimmer()
            binding.shimmerPaymentShippingAddress.visibility = View.GONE
            binding.constraintPaymentShippingAddress.visibility = View.VISIBLE
        }
    }

    private fun showNoShippingDataMessage() {
        binding.run {
            textViewPaymentShippingAddressNull.visibility = View.VISIBLE
            textViewPaymentShippingName.visibility = View.GONE
            textViewPaymentShippingAddress1.visibility = View.GONE
            textViewPaymentShippingAddress2.visibility = View.GONE
            textViewPaymentShippingPhone.visibility = View.GONE
        }
    }


    // 배송지 정보 가져오기
    private fun getShippingAddress() {
        showSampleData(true)
        // 기본 배송지
        if (!shippingViewState) {
            shippingManagementRepository.getBasicShippingAddress { basicFieldValue ->
                // 기본배송지 설정되어있지 않을 때
                if(basicFieldValue == ""){
                    showNoShippingDataMessage()
                    showSampleData(false)
                }
                else {
                    shippingManagementRepository.getShippingData(basicFieldValue,
                        onSuccess = { shippingData ->
                            shippingData?.let { data ->
                                order.shippingAddress = hashMapOf(
                                    "address" to data.address,
                                    "address_detail" to data.addressDetail,
                                    "phone_number" to data.phoneNumber,
                                    "recipient" to data.recipient
                                )

                                binding.run {
                                    textViewPaymentShippingName.text = data.shippingName
                                    textViewPaymentShippingAddress1.text = data.address
                                    textViewPaymentShippingAddress2.text = data.addressDetail
                                    textViewPaymentShippingPhone.text = data.phoneNumber
                                }

                                showSampleData(false)
                            } ?: run {
                                // 데이터가 null인 경우 처리
                                Log.e("paymentFragment", "해당 배송지 데이터 없음")
                                showNoShippingDataMessage()
                                showSampleData(false)
                            }
                        }
                    )
                }
            }
        } else { // 배송지 변경으로 가져온 배송지
            shippingManagementRepository.getShippingData(changeId,
                onSuccess = { shippingData ->
                    shippingData?.let { data ->
                        order.shippingAddress = hashMapOf(
                            "address" to data.address,
                            "address_detail" to data.addressDetail,
                            "phone_number" to data.phoneNumber,
                            "recipient" to data.recipient
                        )
                        binding.run {
                            textViewPaymentShippingName.text = data.shippingName
                            textViewPaymentShippingAddress1.text = data.address
                            textViewPaymentShippingAddress2.text = data.addressDetail
                            textViewPaymentShippingPhone.text = data.phoneNumber
                        }
                    } ?: run {
                        // 데이터가 null인 경우 처리
                        Log.e("paymentFragment", "해당 배송지 데이터 없음")
                        showNoShippingDataMessage()
                        showSampleData(false)
                    }

                    showSampleData(false)
                }
            )
        }
    }

    private fun setPriceTextView() {
        // 배송비
        order.deliveryFee = 3000.toString()
        // 상품 총 가격
        val productPrice = prodInfo.price.toLong() * prodInfo.count
        // 총 가격
        order.totalPrice = (productPrice + order.deliveryFee.toLong()).toString()

        binding.run {
            "${dec.format(productPrice)}원".also {
                textViewPaymentAmountValue.text = it
            }

            "${dec.format(order.deliveryFee.toLong())}원".also {
                textViewPaymentShippingFeeValue.text = it
            }

            "${dec.format(order.totalPrice.toLong())}원".also {
                textViewPaymentTotalMountValue.text = it
            }

            "${dec.format(order.totalPrice.toLong())}원 결제하기".also {
                buttonPaymentPayment.text = it
            }
        }

    }

    private fun uploadOrderFloorPlans(orderDate: String) {
        val productFloorPlanFileNameList = mutableMapOf<String, String?>()

        var successfulUploads = 0
        val totalUploads = prodInfo.diagram.size
        prodInfo.diagram.forEach {
            if (it.value != null) {
                val orderFloorPlanFileName =
                    "orderFloorPlan/$orderDate/${MainActivity.getMilliSec()}.jpg"
                productFloorPlanFileNameList[it.key] = orderFloorPlanFileName
                storageRef.child(orderFloorPlanFileName).putFile(it.value!!.toUri())
                    .addOnSuccessListener {
                        successfulUploads++

                        if(successfulUploads == totalUploads) {
                            registerOrderData(orderDate)
                        }
                    }
            } else {
                productFloorPlanFileNameList[it.key] = null
            }
        }
        prodInfo.diagram = productFloorPlanFileNameList as HashMap<String, String?>
    }

    private fun registerOrderData(orderDate: String) {
        binding.progressBarPaymentLoading.visibility = View.VISIBLE
        // 주문번호 랜덤 2자리
        val randomChars = (1..2)
            .map { ('a'..'z').toList() + ('A'..'Z').toList() + ('0'..'9').toList() }
            .flatten()
            .shuffled()
            .take(2)
            .joinToString("")
        order.orderNum = orderDate + randomChars
        order.state = "제작 대기"
        order.buyerId = UserModel.roleId

        val orderMap = hashMapOf(
            "date" to orderDate,
            "deliveryFee" to order.deliveryFee,
            "orderNum" to order.orderNum, // 15자리
            "shippingAddress" to order.shippingAddress,
            "state" to order.state,
            "totalPrice" to order.totalPrice,
            "buyerId" to order.buyerId
        )

        val prodInfoMap = hashMapOf<String, Any?>()
        // 커스텀 상품 일때
        if (prodInfo.isCustom) {
            prodInfoMap["prodInfoId"] = prodInfo.prodInfoId
            prodInfoMap["count"] = prodInfo.count
            prodInfoMap["option"] = prodInfo.option
            prodInfoMap["price"] = prodInfo.price
            prodInfoMap["diagram"] = prodInfo.diagram
            prodInfoMap["customWord"] = prodInfo.customWord
            prodInfoMap["customLocation"] = prodInfo.customLocation
        } else{
            // 기성품 일때
            prodInfoMap["prodInfoId"] = prodInfo.prodInfoId
            prodInfoMap["count"] = prodInfo.count
        }

        // 주문정보 저장
        db.collection("order").add(orderMap).addOnCompleteListener {
            order.orderId = it.result.id

            // 주문정보 - 상품정보 저장
            db.collection("order").document(it.result.id).collection("prod_Info").document(orderDate)
                .set(prodInfoMap).addOnSuccessListener {

                    // 상품정보의 salesQuantity 가져오기
                    db.collection("product").document(prodInfo.prodInfoId).get().addOnSuccessListener { documentSnapshot ->
                        val salesQuantity = documentSnapshot.getLong("sales_quantity")

                        // 상품정보 salesQuantity 업데이트
                        if (salesQuantity != null) {

                            db.collection("product").document(prodInfo.prodInfoId).update("sales_quantity",salesQuantity.plus(1L)).addOnSuccessListener {
                                binding.progressBarPaymentLoading.visibility = View.GONE

                                val bundle = Bundle().apply {
                                    putString("orderId", order.orderNum)
                                    putString("orderRecipient", order.shippingAddress["recipient"])
                                    putString("orderMobile", order.shippingAddress["phone_number"])
                                    putString("orderAddress", order.shippingAddress["address"])
                                    putString("orderAddress_detail", order.shippingAddress["address_detail"])
                                }

                                findNavController()
                                    .navigate(R.id.action_paymentFragment_to_purchaseCompleteFragment, bundle)
                            }
                        }
                    }
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
            val bundle = Bundle().apply {
                putBoolean("payment_to_shippingManagement", true)
            }
            findNavController().navigate(
                R.id.action_paymentFragment_to_shippingManagementFragment,
                bundle
            )
        }
    }

    // 결제 상태 enum 클래스
//    enum class

}