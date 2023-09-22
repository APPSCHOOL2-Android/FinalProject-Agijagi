package likelion.project.agijagi.chatting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.MainActivity.Companion.getMilliSec
import likelion.project.agijagi.databinding.FragmentChattingRoomBinding
import likelion.project.agijagi.model.Message
import likelion.project.agijagi.model.UserModel

class ChattingRoomFragment : Fragment() {

    private var _binding: FragmentChattingRoomBinding? = null
    private val binding get() = _binding!!

    lateinit var chattingRoomAdapter: ChattingRoomAdapter

    val db = Firebase.firestore

    var roomId: String? = ""
    lateinit var message: Message
    private val chattingList = mutableListOf<Message>()

    companion object {
        var brand: String? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChattingRoomBinding.inflate(inflater)

        setupToolbar()
        checkChattingRoom()
        sendMessage()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chattingRoomAdapter = ChattingRoomAdapter(UserModel.roleId)
        brand = getBrand()
    }

    private fun getSellerId(): String {
        return arguments?.getString("sellerId").toString()
    }

    private fun getBrand(): String {
        return arguments?.getString("brand").toString()
    }

    private fun setupRecyclerViewChattingRoom(list: List<Message>) {
        binding.recyclerviewChattingRoom.run {
            adapter = chattingRoomAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
                reverseLayout = false
            }
        }
        chattingRoomAdapter.submitList(list)
    }

    private fun checkChattingRoom() {
        db.collection("chatting_room")
            .whereEqualTo("buyer_id", UserModel.roleId)
            .whereEqualTo("seller_id", getSellerId())
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.forEach {
                    Log.d("hye", it.id)
                    roomId = it.id
                }
                getMessage(roomId!!)
                if (roomId == "") {
                    createChattingRoom()
                }
            }
    }

    private fun sendMessage() {
        binding.run {
            imageButtonChattingRoomSend.setOnClickListener {
                val content = edittextChattingRoom.text.toString()
                message = Message(getMilliSec(), content, true, UserModel.roleId)
                val messageMap = hashMapOf(
                    "date" to message.date,
                    "content" to message.content,
                    "isRead" to message.isRead,
                    "writer" to message.writer
                )
                db.collection("chatting_room")
                    .document(roomId!!)
                    .collection("message")
                    .document(getMilliSec())
                    .set(messageMap)

                chattingList.add(message)
                edittextChattingRoom.setText("")
                setupRecyclerViewChattingRoom(chattingList)
            }
        }
    }

    private fun getMessage(roomId: String) {
        db.collection("chatting_room")
            .document(roomId)
            .collection("message")
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    Log.d("hye", document.id)
                    message = Message(
                        document["date"].toString(),
                        document["content"].toString(),
                        document.getBoolean("isRead")!!,
                        document["writer"].toString()
                    )
                    chattingList.add(message)
                }
                setupRecyclerViewChattingRoom(chattingList)
            }
    }

    private fun createChattingRoom() {
        val chatRoomUserInfo = hashMapOf(
            "buyer_id" to UserModel.roleId,
            "seller_id" to getSellerId()
        )
        db.collection("chatting_room").document().set(chatRoomUserInfo)
    }

    private fun setupToolbar() {
        binding.toolbarChattingRoom.run {
            // 로그인 유저 구매자일 때 -> title: 브랜드명
            // 판매자 일때 -> title: 구매자 닉네임
            title = if (!UserModel.isSeller!!) {
                getBrand()
            } else {
                "구매자 닉네임"
            }
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}