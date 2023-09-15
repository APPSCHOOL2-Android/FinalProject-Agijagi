package likelion.project.agijagi.model

object SellerModel {
    var sellerId: String = ""
    var address: String = ""
    var brCert: String = ""
    var brn: String = ""
    var businessName: String = ""
    lateinit var notifSetting: MutableMap<String, Boolean>
    var tel: String = ""
}
