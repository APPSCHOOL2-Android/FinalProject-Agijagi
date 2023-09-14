package likelion.project.agijagi.model

data class BuyerModel(
    var shippingAddress: ShippingAddress,
    val wish: ArrayList<String>,
    var basic: String,
    var nickname: String,
    var notifSetting: MutableMap<String, Boolean>
)

data class ShippingAddress(
    var shippingAddressId: String,
    var address: String,
    var addressDetail: String,
    var phoneNumber: String,
    var recipient: String,
    var shippingName: String
)



