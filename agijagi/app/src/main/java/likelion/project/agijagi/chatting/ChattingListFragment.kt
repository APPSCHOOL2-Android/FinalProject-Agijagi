package likelion.project.agijagi.chatting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentChattingListBinding
import likelion.project.agijagi.model.UserModel

class ChattingListFragment : Fragment() {

    private var _binding: FragmentChattingListBinding? = null
    private val binding get() = _binding!!

    lateinit var mainActivity: MainActivity
    lateinit var chattingListAdapter: ChattingListAdapter

    val db = Firebase.firestore

    lateinit var chattingListModel: ChattingListModel

    private val chattingRoomList = mutableListOf<ChattingListModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChattingListBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        chattingListAdapter = ChattingListAdapter()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        getChattingRoomList()

        binding.run {
            // 초기화
            buttonChattingListCancel.setOnClickListener {
                changeView(true)
            }

            // 선택된 메뉴 지우기
            buttonChattingListDelete.setOnClickListener {
                changeView(true)
            }
        }
    }

    private fun setupToolbar() {
        binding.run {
            materialToolbarChattingList.run {
                setNavigationOnClickListener {
                    findNavController().popBackStack()
                }

                setOnMenuItemClickListener {
                    changeView(false)
                    false
                }
            }
        }
    }

    private fun setupRecyclerViewChattingList() {
        binding.recyclerviewChattingList.run {
            adapter = chattingListAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                addItemDecoration(
                    MaterialDividerItemDecoration(
                        context,
                        MaterialDividerItemDecoration.VERTICAL
                    )
                )
                reverseLayout = false
            }
            chattingListAdapter.submitList(chattingRoomList)
        }
    }

    private fun getChattingRoomList() {
        chattingRoomList.clear()
        // seller 채팅룸
        if (UserModel.isSeller == true) {
            db.collection("chatting_room")
                .whereEqualTo("seller_id", UserModel.roleId)
                .get()
                .addOnSuccessListener {
                    for (document in it) {
                        val buyerId = document.getString("buyer_id").toString()
                        db.collection("buyer").get().addOnSuccessListener { buyerDocuments ->
                            for (documentBuyer in buyerDocuments) {
                                if (documentBuyer.id == buyerId) {
                                    db.collection("chatting_room")
                                        .document(document.id)
                                        .collection("message").get()
                                        .addOnSuccessListener { querySnapshot ->
                                            val messageDocuments = querySnapshot.documents
                                            if (messageDocuments.isNotEmpty()) {
                                                // 문서를 역순으로 정렬
                                                messageDocuments.sortByDescending { it.id }

                                                // 마지막 문서 가져오기
                                                val lastMessage = messageDocuments[0]
                                                chattingListModel = ChattingListModel(
                                                    document.data["buyer_id"].toString(),
                                                    document.data["seller_id"].toString(),
                                                    documentBuyer["nickname"].toString(),
                                                    lastMessage.data!!["content"].toString(),
                                                    lastMessage.data!!["date"].toString(),
                                                    lastMessage.getBoolean("isRead")!!,
                                                    true
                                                )
                                                chattingRoomList.add(chattingListModel)
                                                setupRecyclerViewChattingList()
                                                changeViewWhenNoChatList()
                                            }
                                        }
                                }
                            }
                        }
                    }
                }
        } else {
            // buyer 채팅룸
            db.collection("chatting_room")
                .whereEqualTo("buyer_id", UserModel.roleId)
                .get()
                .addOnSuccessListener {
                    for (document in it) {
                        val sellerId = document.getString("seller_id").toString()
                        db.collection("seller").get().addOnSuccessListener { sellerDocuments ->
                            for (documentSeller in sellerDocuments) {
                                if (documentSeller.id == sellerId) {
                                    db.collection("chatting_room")
                                        .document(document.id)
                                        .collection("message").get()
                                        .addOnSuccessListener { querySnapshot ->
                                            val messageDocuments = querySnapshot.documents
                                            if (messageDocuments.isNotEmpty()) {
                                                // 문서를 역순으로 정렬
                                                messageDocuments.sortByDescending { it.id }

                                                // 마지막 문서 가져오기
                                                val lastMessage = messageDocuments[0]
                                                chattingListModel = ChattingListModel(
                                                    document.data["buyer_id"].toString(),
                                                    document.data["seller_id"].toString(),
                                                    documentSeller["bussiness_name"].toString(),
                                                    lastMessage.data!!["content"].toString(),
                                                    lastMessage.data!!["date"].toString(),
                                                    lastMessage.getBoolean("isRead")!!,
                                                    true
                                                )

                                                chattingRoomList.add(chattingListModel)
                                                setupRecyclerViewChattingList()
                                                changeViewWhenNoChatList()
                                            }
                                        }
                                }
                            }
                        }
                    }
                }
        }
    }

    private fun changeViewWhenNoChatList() {
        binding.textviewChattingListEmptyMsg.visibility =
            if (chattingRoomList.size <= 0) VISIBLE else GONE
    }

    private fun changeView(isListView: Boolean) {
        binding.run {
            if (isListView) {
                layoutChattingListBottomButton.visibility = GONE
                materialToolbarChattingList.run {
                    menu.findItem(R.id.menu_notification_list_delete).isVisible = true
                    setNavigationIcon(R.drawable.arrow_back_24px)
                }

                // 체크박스 숨기기
                chattingListAdapter.updateCheckbox(false)
                chattingListAdapter.notifyDataSetChanged()
            } else {
                layoutChattingListBottomButton.visibility = VISIBLE
                materialToolbarChattingList.run {
                    menu.findItem(R.id.menu_notification_list_delete).isVisible = false
                    navigationIcon = null
                }

                // 체크박스 보이기
                chattingListAdapter.updateCheckbox(true)
                chattingListAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}