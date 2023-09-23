package likelion.project.agijagi.notification

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.ItemNotificationListBinding
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationListAdapter(val context: Context) :
    ListAdapter<NotificationListModel, NotificationListAdapter.NotificationListViewHolder>(diffUtil) {
    private var isTrashCan = false
    lateinit var actionCheckBoxParentState: () -> Unit
    lateinit var actionGoToChat: (roomID: String) -> Unit
    lateinit var actionUpdateIsRead: (notifID: String) -> Unit

    inner class NotificationListViewHolder(val bind: ItemNotificationListBinding) :
        RecyclerView.ViewHolder(bind.root) {

        fun bind(item: NotificationListModel) {
            with(bind) {
                val params = checkboxNotificationList.layoutParams
                params.width = if (isTrashCan) ViewGroup.LayoutParams.WRAP_CONTENT else 0
                checkboxNotificationList.layoutParams = params

                textViewNotificationListSender.text = item.sender
                textViewNotificationListBody.text = item.content
                imageViewNotificationListNew.visibility =
                    if (item.isRead) View.GONE else View.VISIBLE

                // 날자 계산 필요. pattern: yyMMddHHmmssSSS
                val date = SimpleDateFormat("yyMMddHHmmssSSS").parse(item.date)
                val now = MainActivity.getMilliSec()
                val year = now.substring(0, 2).toInt() - item.date.substring(0, 2).toInt()
                val month = now.substring(2, 4).toInt() - item.date.substring(2, 4).toInt()
                val day = now.substring(4, 6).toInt() - item.date.substring(4, 6).toInt()

                var dateStr = ""
                if (1 < year) {
                    // 1년이 지났다면 연도로 표기
                    if (month < 1) {
                        dateStr = SimpleDateFormat("M월 d일", Locale.getDefault()).format(date)
                    } else {
                        dateStr = SimpleDateFormat("YYYY년", Locale.getDefault()).format(date)
                    }
                } else if (1 < day) {
                    // 1년이 지나지 않았다면 00월 00일로 표기
                    dateStr = SimpleDateFormat("M월 d일", Locale.getDefault()).format(date)
                } else if (1 == day) {
                    dateStr = "어제"
                } else {
                    // 하루 이내의 시간은 오전/후 hh:mm 로 표기
                    dateStr = SimpleDateFormat("aa h:m", Locale.getDefault()).format(date)
                }
                textViewNotificationListDate.text = dateStr

                if (isTrashCan) {
                    checkboxNotificationList.isChecked = item.isCheck
                    actionCheckBoxParentState?.invoke()
                }

                // 아이템 클릭 시 동작 구현
                when (item.type) {
                    NotificationType.NOTIF_MESSAGE.str -> {
                        textViewNotificationListSender.text = item.sender
                        textViewNotificationListBody.text = item.content

                        // 클릭 시 다이얼로그 생성
                        root.setOnClickListener {
                            if (!item.isRead) {
                                actionUpdateIsRead(item.id)
                                item.isRead = true
                                notifyDataSetChanged()
                            }

                            // customTitle
                            val customTitle = View.inflate(
                                context,
                                R.layout.dialog_notification_message_custom,
                                null
                            )
                            val title =
                                customTitle.findViewById<TextView>(R.id.textView_notification_dialog_customTitle)
                            title.text = item.sender
                            val subTitle =
                                customTitle.findViewById<TextView>(R.id.textView_notification_dialog_subTitle)

                            subTitle.text = SimpleDateFormat(
                                "yyyy년 M월 d일 aa h:m", Locale.getDefault()
                            ).format(date)

                            MaterialAlertDialogBuilder(context as MainActivity)
                                .setCustomTitle(customTitle)
                                .setMessage(item.content)
                                .setPositiveButton("확인") { dialog, which ->
                                    // Respond to positive button press
                                }
                                .show()
                        }
                    }

                    NotificationType.NOTIF_CHAT.str -> {
                        textViewNotificationListSender.text = item.sender
                        textViewNotificationListBody.text = "채팅으로 이동합니다"

                        // 클릭 시 채팅으로 이동
                        root.setOnClickListener {
                            if (!item.isRead) {
                                actionUpdateIsRead(item.id)
                                item.isRead = true
                                notifyDataSetChanged()
                            }

                            actionGoToChat(item.content) //item.content == roomID
                        }
                    }
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationListViewHolder {
        val itemNotificationlistBinding =
            ItemNotificationListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = NotificationListViewHolder(itemNotificationlistBinding)

        return viewHolder
    }

    override fun onBindViewHolder(holder: NotificationListViewHolder, position: Int) {
        holder.bind(currentList[position])
        holder.bind.checkboxNotificationList.setOnClickListener {
            currentList[position].isCheck = !currentList[position].isCheck
            actionCheckBoxParentState()
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<NotificationListModel>() {
            override fun areItemsTheSame(
                oldItem: NotificationListModel,
                newItem: NotificationListModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: NotificationListModel,
                newItem: NotificationListModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    fun updateCheckbox(state: Boolean) {
        isTrashCan = state
    }

    fun setCheckBoxParentState(action: () -> Unit) {
        actionCheckBoxParentState = action
    }

    fun setGoToChat(action: (roomID: String) -> Unit) {
        actionGoToChat = action
    }

    fun setUpdateIsRead(action: (notifID: String) -> Unit) {
        actionUpdateIsRead = action
    }
}