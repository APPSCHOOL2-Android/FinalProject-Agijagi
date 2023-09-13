package likelion.project.agijagi.chatting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.ItemChattingListBinding

class ChattingListAdapter :
    ListAdapter<ChattingListModel, ChattingListAdapter.ChattingListViewHolder>(diffUtil) {
    private var isTrashCan = false

    inner class ChattingListViewHolder(val bind: ItemChattingListBinding) :
        RecyclerView.ViewHolder(bind.root) {

        fun bind(item: ChattingListModel) {
            with(bind) {
                if (isTrashCan) {
                    checkboxChattingList.visibility = View.VISIBLE
                } else {
                    checkboxChattingList.visibility = View.GONE
                }
                textviewChattingListTitle.text = item.sender
                textviewChattingListBody.text = item.content
                imageviewChattingListNew.visibility =
                    if (item.isRead) View.GONE else View.VISIBLE

                // 날자 계산 필요.
                // 0월 0일
                // 어제
                // 오전 00:00
                // 현재~어제 날자까지는 별도로 표시. 그 이전은 날자로 표시.
                textviewChattingListDate.text = item.date

                root.setOnClickListener {
                    it.findNavController()
                        .navigate(R.id.action_chattingListFragment_to_chattingRoomFragment)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChattingListViewHolder {
        val itemChattingListBinding =
            ItemChattingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = ChattingListViewHolder(itemChattingListBinding)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ChattingListViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChattingListModel>() {
            override fun areItemsTheSame(
                oldItem: ChattingListModel,
                newItem: ChattingListModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ChattingListModel,
                newItem: ChattingListModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    fun updateCheckbox(state: Boolean) {
        isTrashCan = state
    }

}