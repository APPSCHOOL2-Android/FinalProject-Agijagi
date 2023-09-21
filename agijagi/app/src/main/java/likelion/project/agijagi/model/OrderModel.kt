package likelion.project.agijagi.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class OrderModel(
    var orderId: String,
    var prodInfo: ProdInfo?,
    var date: String,
    var deliveryFee: String,
    var orderNum: String,
    var shippingAddress: MutableMap<String, String>,
    var state: String,
    var totalPrice: String,
    var buyerId: String
)

@Parcelize
data class ProdInfo(
    var isCustom: Boolean,
    var prodInfoId: String,
    var count: Long,
    var diagram: HashMap<String, String?>,
    var option: String,
    var price: String,
    var customWord: String?,
    var customLocation: String?
) : Parcelable
