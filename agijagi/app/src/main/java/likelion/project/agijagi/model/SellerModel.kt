package likelion.project.agijagi.model

data class SellerModel(
    var sellerId: String,
    var address: String,
    var brCert: String,
    var brn: String,
    var businessName: String,
    var notifSetting: MutableMap<String, Boolean>,
    var tel: String,
)
