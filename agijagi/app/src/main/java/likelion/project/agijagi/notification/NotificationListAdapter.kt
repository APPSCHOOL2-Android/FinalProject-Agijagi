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
    lateinit var actionGoToChat: (roomID: String, sender: String) -> Unit
    lateinit var actionUpdateIsRead: (notifID: String) -> Unit

    inner class NotificationListViewHolder(val bind: ItemNotificationListBinding) :
        RecyclerView.ViewHolder(bind.root) {

        fun bind(item: NotificationListModel) {
            with(bind) {
                val params = checkboxNotificationList.layoutParams
                params.width = if (isTrashCan) ViewGroup.LayoutParams.WRAP_CONTENT else 0
                checkboxNotificationList.layoutParams = params

                textViewNotificationListSender.text = item.senderName
                textViewNotificationListBody.text = item.content
                imageViewNotificationListNew.visibility =
                    if (item.isRead) View.GONE else View.VISIBLE
                textViewNotificationListDate.text = item.dateStr

                if (isTrashCan) {
                    checkboxNotificationList.isChecked = item.isCheck
                    actionCheckBoxParentState?.invoke()
                }

                // 아이템 클릭 시 동작 구현
                when (item.type) {
                    NotificationType.NOTIF_MESSAGE.str -> {
                        textViewNotificationListSender.text = item.senderName
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
                            title.text = item.senderName
                            val subTitle =
                                customTitle.findViewById<TextView>(R.id.textView_notification_dialog_subTitle)

                            subTitle.text = SimpleDateFormat(
                                "yyyy년 M월 d일 aa h:m", Locale.getDefault()
                            ).format(SimpleDateFormat("yyMMddHHmmssSSS").parse(item.date))

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
                        textViewNotificationListSender.text = item.senderName
                        textViewNotificationListBody.text = "채팅으로 이동합니다"

                        // 클릭 시 채팅으로 이동
                        root.setOnClickListener {
                            if (!item.isRead) {
                                actionUpdateIsRead(item.id)
                                item.isRead = true
                                notifyDataSetChanged()
                            }

                            actionGoToChat(item.content, item.sender) //item.content == roomID
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

    fun setGoToChat(action: (roomID: String, sender: String) -> Unit) {
        actionGoToChat = action
    }

    fun setUpdateIsRead(action: (notifID: String) -> Unit) {
        actionUpdateIsRead = action
    }
}