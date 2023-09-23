package likelion.project.agijagi.chatting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import likelion.project.agijagi.MainActivity.Companion.getMilliSec
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

                if (getMilliSec().substring(0, 7) == item.date.substring(0, 7)) {
                    textviewChattingListDate.text = convertToTimeFormat(item.date)
                } else {
                    textviewChattingListDate.text = convertToDateFormat(item.date)
                }

//                imageviewChattingListNew.visibility =
//                    if (item.isRead) View.GONE else View.VISIBLE

                root.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("buyerId", item.buyerId)
                    bundle.putString("sellerId", item.sellerId)
                    bundle.putString("chatRoomTitle", item.sender)
                    it.findNavController()
                        .navigate(R.id.action_chattingListFragment_to_chattingRoomFragment, bundle)
                }
            }
        }
    }

    fun convertToTimeFormat(inputDate: String): String {
        val time = inputDate.substring(6, 10).toLong()
        val hour = time / 100
        val minute = time % 100
        return String.format("%02d:%02d", hour, minute)
    }

    fun convertToDateFormat(inputDate: String): String {
        val time = inputDate.substring(2, 6).toLong()
        val month = time / 100
        val date = time % 100
        return String.format("%02d월 %02d일", month, date)
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