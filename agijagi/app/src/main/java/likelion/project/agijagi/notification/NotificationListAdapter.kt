package likelion.project.agijagi.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import likelion.project.agijagi.databinding.ItemNotificationListBinding

class NotificationListAdapter :
    ListAdapter<NotificationListModel, NotificationListAdapter.NotificationListViewHolder>(diffUtil) {
    private var isTrashCan = false

    inner class NotificationListViewHolder(val bind: ItemNotificationListBinding) :
        RecyclerView.ViewHolder(bind.root) {

        fun bind(item: NotificationListModel) {
            with(bind) {
                if (isTrashCan) {
                    checkboxNotificationList.visibility = View.VISIBLE
                } else {
                    checkboxNotificationList.visibility = View.GONE
                }
                textViewNotificationListTitle.text = item.title
                textViewNotificationListBody.text = item.content
                imageViewNotificationListNew.visibility =
                    if (item.isRead) View.GONE else View.VISIBLE

                // 날자 계산 필요.
                // 0월 0일
                // 어제
                // 오전 00:00
                // 현재~어제 날자까지는 별도로 표시. 그 이전은 날자로 표시.
                textViewNotificationListDate.text = item.date
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
}