package likelion.project.agijagi.model

object UserModel {
    var uid: String = ""
    var email: String = ""
    var emailNotif: Boolean ?= false
    var googleLoginCheck: Boolean ?= false
    var isSeller:Boolean ?= false
    var name: String = ""
    var newChatCount :Int ? = 0
    var newNotifCount :Int ?= -1
    var password: String = ""
    var roleId: String = ""
    var smsNotif:Boolean ?= false
}