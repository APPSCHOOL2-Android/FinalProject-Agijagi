package likelion.project.agijagi.buyermypage.model

data class ShippingManagementModel(
    val address: String,
    val addressDetail: String,
    val basic : Boolean,
    val title : String,
    val recipientPhone : String,
    val recipient : String
)