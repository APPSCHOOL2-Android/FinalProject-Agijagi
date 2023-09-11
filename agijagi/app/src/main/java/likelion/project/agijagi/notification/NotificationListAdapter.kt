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

class NotificationListAdapter(val context: Context) :
    ListAdapter<NotificationListModel, NotificationListAdapter.NotificationListViewHolder>(diffUtil) {
    private var isTrashCan = false
    lateinit var checkBoxParentSetting: () -> Unit
    lateinit var goToChat: (roomID: String) -> Unit

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

                // 날자 계산 필요.
                // 0월 0일
                // 어제
                // 오전 00:00
                // 현재~어제 날자까지는 별도로 표시. 그 이전은 날자로 표시.
                val dateStr: String = item.date
                textViewNotificationListDate.text = dateStr

                if (isTrashCan) {
                    checkboxNotificationList.isChecked = item.isCheck
                    checkBoxParentSetting?.invoke()
                }

                // 아이템 클릭 시 동작 구현
                when (item.type) {
                    NotificationType.NOTIF_MESSAGE.str -> {
                        textViewNotificationListSender.text = item.sender
                        textViewNotificationListBody.text = item.content

                        // 클릭 시 다이얼로그 생성
                        root.setOnClickListener {
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
                            subTitle.text = dateStr

                            MaterialAlertDialogBuilder(context as MainActivity)
                                .setCustomTitle(customTitle)
                                .setMessage(item.content)
                                .setPositiveButton("확인") { dialog, which ->
                                    // Respond to positive button press
                                    item.isRead = true
                                    notifyDataSetChanged()
                                }
                                .show()
                        }
                    }

                    NotificationType.NOTIF_CHAT.str -> {
                        textViewNotificationListSender.text = item.sender
                        textViewNotificationListBody.text = "채팅으로 이동합니다"

                        // 클릭 시 채팅으로 이동
                        root.setOnClickListener {
                            item.isRead = true
                            notifyDataSetChanged()
                            goToChat(item.content) //item.content == roomID
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
            checkBoxParentSetting()
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

    fun setCheckBoxParentStete(action: () -> Unit) {
        checkBoxParentSetting = action
    }

    fun setgoToChat(action: (roomID: String) -> Unit) {
        goToChat = action
    }
}