package likelion.project.agijagi.notification

data class NotificationListModel(
    var id: String, // 알림 pk
    var sender: String, //보낸 이
    var senderName: String, // seller BussinessName, buyer nickName, system agijagi
    var content: String, // 본문
    var date: String, // 보낸 시간 (yyMMddHHmmssSSS)
    var dateStr: String, // 화면에 표시할 값
    var type: String = "message", // NotificationType.str
    var isRead: Boolean = false, // 확인 여부 (false: new!)
    var isCheck: Boolean = false // 삭제 시 사용
)

enum class NotificationType(val idx: Int, val str: String) {
    NOTIF_MESSAGE(0, "message"),
    NOTIF_CHAT(1, "chat"),
}