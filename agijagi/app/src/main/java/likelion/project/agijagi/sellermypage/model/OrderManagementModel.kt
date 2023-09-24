package likelion.project.agijagi.sellermypage.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderManagementModel(
    val brand: String,
    val name: String,
    val price: String,
    val thumbnailImage:String,
    val date : String,
    var orderId:String,
    var productId:String,
    var userName:String,
    var totalPrice:String
) : Parcelable