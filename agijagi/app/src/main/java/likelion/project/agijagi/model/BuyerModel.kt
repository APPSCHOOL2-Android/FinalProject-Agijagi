package likelion.project.agijagi.model

object BuyerModel {
    lateinit var shippingAddress: ShippingAddress
    lateinit var wish: ArrayList<String>
    var basic: String = ""
    var nickname: String = ""
    lateinit var notifSetting: MutableMap<String, Boolean>
}

data class ShippingAddress(
    var shippingAddressId: String,
    var address: String,
    var addressDetail: String,
    var phoneNumber: String,
    var recipient: String,
    var shippingName: String
)



