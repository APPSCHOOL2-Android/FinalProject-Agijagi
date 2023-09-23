package likelion.project.agijagi.model

data class ChattingRoomModel(
    var roomId: String,
    var message: Message,
    var buyerId: String,
    var sellerId: String
)

data class Message(
    var date: String,
    var content: String,
    var isRead: Boolean,
    var writer: String
)

enum class ChatState {
    MY_CHAT,
    OTHER_CHAT
}
