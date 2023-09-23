package likelion.project.agijagi.chatting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import likelion.project.agijagi.chatting.ChattingRoomFragment.Companion.writerName
import likelion.project.agijagi.databinding.ItemChattingRoomMyChatBinding
import likelion.project.agijagi.databinding.ItemChattingRoomOtherChatBinding
import likelion.project.agijagi.model.ChatState
import likelion.project.agijagi.model.Message
import likelion.project.agijagi.model.UserModel

class ChattingRoomAdapter(
    val roleId: String
) : ListAdapter<Message, RecyclerView.ViewHolder>(diffUtil) {

    inner class MyChatItemViewHolder(private val binding: ItemChattingRoomMyChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.textviewChattingRoomMyChatContent.text = message.content
            binding.textviewChattingRoomMyChatDate.text = convertToTimeFormat(message.date)
            if (!message.isRead) {
                binding.textviewChattingRoomIsRead.text = "1"
            }
        }
    }

    inner class OtherChatItemViewHolder(private val binding: ItemChattingRoomOtherChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.textviewChattingRoomOtherChatWriter.text = writerName
            binding.textviewChattingRoomOtherChatContent.text = message.content
            binding.textviewChattingRoomOtherChatDate.text = convertToTimeFormat(message.date)
        }
    }

    fun convertToTimeFormat(inputDate: String): String {
        val date = inputDate.substring(6, 10).toLong()
        val hour = date / 100
        val minute = date % 100
        return String.format("%02d:%02d", hour, minute)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ChatState.MY_CHAT.ordinal) {
            MyChatItemViewHolder(
                ItemChattingRoomMyChatBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            OtherChatItemViewHolder(
                ItemChattingRoomOtherChatBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ChatState.MY_CHAT.ordinal) {
            (holder as MyChatItemViewHolder).bind(currentList[position])
        } else {
            (holder as OtherChatItemViewHolder).bind((currentList[position]))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (UserModel.roleId == currentList[position].writer)
            ChatState.MY_CHAT.ordinal
        else ChatState.OTHER_CHAT.ordinal
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem == newItem
            }
        }
    }

}