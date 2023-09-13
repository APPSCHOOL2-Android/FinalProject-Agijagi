package likelion.project.agijagi.model

data class OrderModel(
    var orderId: String,
    var prodInfo: ProdInfo,
    var date: String,
    var deliveryFee: String,
    var orderNum: String,
    var shippingAddress: MutableMap<String, String>,
    var state: String,
    var totalPrice: String,
    var buyerId: String
)

data class ProdInfo(
    var prodInfoId: String,
    var count: Long,
    var diagram: ArrayList<String>,
    var option: String,
    var price: String
)


