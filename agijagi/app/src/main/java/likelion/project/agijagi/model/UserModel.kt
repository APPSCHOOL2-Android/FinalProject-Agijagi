package likelion.project.agijagi.model

data class UserModel(
    var uid: String,
    var email: String,
    var emailNotif: String,
    var googleLoginCheck: Boolean,
    var isSeller: Boolean,
    var name: String,
    var newChatCount: Long,
    var newNotifCount: Long,
    var password: String,
    var roleId: String,
    var smsNotif: Boolean
)
