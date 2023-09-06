package likelion.project.agijagi.notification

data class NotificationListModel(
    var sender: String, //보낸 이
    var title: String, // 제목
    var content: String, // 본문
    var date: String, // 보낸 시간 (dd-MM-yyyy HH:mm:ss)
    var isRead: Boolean = false, // 확인 여부 (false: new!)
    var isCheck: Boolean = false // 삭제 시 사용
)