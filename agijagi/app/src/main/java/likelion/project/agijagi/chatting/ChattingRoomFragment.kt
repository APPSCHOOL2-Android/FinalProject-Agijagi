package likelion.project.agijagi.chatting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.MainActivity.Companion.getMilliSec
import likelion.project.agijagi.databinding.FragmentChattingRoomBinding
import likelion.project.agijagi.model.Message
import likelion.project.agijagi.model.UserModel

class ChattingRoomFragment : Fragment() {

    private var _binding: FragmentChattingRoomBinding? = null
    private val binding get() = _binding!!

    private lateinit var chattingRoomAdapter: ChattingRoomAdapter

    val db = Firebase.firestore

    var chatRoomId: String? = ""

    lateinit var message: Message
    private val chattingList = mutableListOf<Message>()

    private var messageListener: ListenerRegistration? = null

    companion object {
        var writerName = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChattingRoomBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chattingRoomAdapter = ChattingRoomAdapter(UserModel.roleId)
        writerName = getChatRoomTitle()
        setupToolbar()
        checkChattingRoom()
        sendMessage()
    }

    private fun getSellerId(): String {
        return arguments?.getString("sellerId").toString()
    }

    private fun getBuyerId(): String {
        return arguments?.getString("buyerId").toString()
    }

    private fun getChatRoomTitle(): String {
        return arguments?.getString("chatRoomTitle").toString()
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
            .whereEqualTo("buyer_id", getBuyerId())
            .whereEqualTo("seller_id", getSellerId())
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.forEach {
                    chatRoomId = it.id
                    if (chatRoomId == "") {
                        createChattingRoom()
                    } else {
                        startChattingListener(chatRoomId!!)
                    }
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
                    .document(chatRoomId!!)
                    .collection("message")
                    .document(getMilliSec())
                    .set(messageMap)

                Log.d("ChattingRoomFragment.sendMessage()", chatRoomId.toString())

                edittextChattingRoom.setText("")
                // 메시지를 Firestore에 추가한 후 Firestore 리스너를 통해 자동으로 반영
            }
        }
    }

    private fun startChattingListener(roomId: String) {
        // Firestore 리스너를 시작하여 메시지를 실시간으로 업데이트
        messageListener = db.collection("chatting_room")
            .document(roomId)
            .collection("message")
            .orderBy("date")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirebaseException", "Error listening for messages: $error")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // snapshot의 변경 내용을 처리
                    for (documentChange in snapshot.documentChanges) {
                        when (documentChange.type) {
                            DocumentChange.Type.ADDED -> {
                                val document = documentChange.document
                                val message = Message(
                                    document["date"].toString(),
                                    document["content"].toString(),
                                    document.getBoolean("isRead")!!,
                                    document["writer"].toString()
                                )
                                chattingList.add(message)
                            }

                            DocumentChange.Type.MODIFIED -> {}
                            DocumentChange.Type.REMOVED -> {}
                        }
                    }
                    setupRecyclerViewChattingRoom(chattingList)
                }
            }
    }

    private fun createChattingRoom() {
        val chatRoomUserInfo = hashMapOf(
            "buyer_id" to getBuyerId(),
            "seller_id" to getSellerId()
        )
        db.collection("chatting_room").document().set(chatRoomUserInfo)
    }

    private fun setupToolbar() {
        binding.toolbarChattingRoom.run {
            title = writerName
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