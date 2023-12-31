package likelion.project.agijagi.chatting

data class ChattingListModel(
    var buyerId: String,
    var sellerId: String,
    var sender: String, //보낸 이
    var content: String, // 본문
    var date: String, // 보낸 시간 (dd-MM-yyyy HH:mm:ss)
    var isRead: Boolean = false, // 확인 여부 (false: new!)
    var isCheck: Boolean = false // 삭제 시 사용
)