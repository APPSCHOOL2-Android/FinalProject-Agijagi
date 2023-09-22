package likelion.project.agijagi.model

object BuyerModel {
    lateinit var shippingAddress: ShippingAddress
    lateinit var wish: ArrayList<String>
    var basic: String = ""
    var nickname: String = ""
    var notifSetting: MutableMap<String, Boolean> = mutableMapOf()
}

data class ShippingAddress(
    var shippingAddressChecked: Boolean, // 결제페이지 배송지 변경시 선택
    var shippingAddressId: String,
    var address: String,
    var addressDetail: String,
    var phoneNumber: String,
    var recipient: String,
    var shippingName: String
)



