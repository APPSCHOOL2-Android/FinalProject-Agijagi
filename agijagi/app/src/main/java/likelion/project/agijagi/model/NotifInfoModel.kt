package likelion.project.agijagi.model

data class NotifInfoModel(
    var notifInfoId: String,
    var content: String,
    var date: String,
    var isChat: Boolean,
    var isDeleted: Boolean,
    var isRead: Boolean,
    var receiver: String,
    var sender: String
)