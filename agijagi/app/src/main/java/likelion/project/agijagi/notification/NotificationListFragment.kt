package likelion.project.agijagi.notification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentNotificationListBinding
import likelion.project.agijagi.model.UserModel
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationListFragment : Fragment() {

    private var _binding: FragmentNotificationListBinding? = null
    private val binding get() = _binding!!

    lateinit var mainActivity: MainActivity
    lateinit var notificationListAdapter: NotificationListAdapter

    private val dataSet = arrayListOf<NotificationListModel>()

    lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationListBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        notificationListAdapter = NotificationListAdapter(mainActivity)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setup()
        getData()
        setViewFunction()
        setToolbarItemAction()
    }

    private fun setToolbarItemAction() {
        binding.materialToolbarNotificationList.run {
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            setOnMenuItemClickListener {
                changeView(false)
                false
            }
        }
    }

    private fun setViewFunction() {
        binding.run {
            // 초기화
            changeView(true)

            recyclerViewNotificationList.run {
                layoutManager = LinearLayoutManager(mainActivity)
                adapter = notificationListAdapter

                addItemDecoration(
                    MaterialDividerItemDecoration(
                        context,
                        MaterialDividerItemDecoration.VERTICAL
                    )
                )
            }
            notificationListAdapter.submitList(dataSet)
            notificationListAdapter.setCheckBoxParentState { setCheckBoxParentStete() }
            notificationListAdapter.setGoToChat { roomID, sender ->
                Log.d(
                    "notificationList to chattingRoom",
                    "roomID = $roomID, sender = $sender, receiver = ${UserModel.roleId}"
                )

                val buyerId = if (UserModel.isSeller!!) sender else UserModel.roleId
                val sellerId = if (!UserModel.isSeller!!) sender else UserModel.roleId

                // 채팅방으로 이동
                val bundle = Bundle()
                bundle.putString("roomID", roomID)
                bundle.putString("buyerId", buyerId)
                bundle.putString("sellerId", sellerId)
                findNavController().navigate(
                    R.id.action_notificationListFragment_to_chattingRoomFragment,
                    bundle
                )
            }
            notificationListAdapter.setUpdateIsRead { notifID ->
                // is_read 필드를 true로 갱신한다
                updateIsRead(notifID)
            }

            // 전체 선택 버튼
            checkboxNotificationListSelectAll.setOnCheckedChangeListener { compoundButton, b ->
                when (checkboxNotificationListSelectAll.checkedState) {
                    MaterialCheckBox.STATE_CHECKED -> {
                        dataSet.forEach { it.isCheck = true }
                    }

                    MaterialCheckBox.STATE_UNCHECKED -> {
                        dataSet.forEach { it.isCheck = false }
                    }
                }

                if (!recyclerViewNotificationList.isComputingLayout) {
                    notificationListAdapter.notifyDataSetChanged()
                }
            }

            materialToolbarNotificationList.run {
                setOnMenuItemClickListener {
                    changeView(false)
                    false
                }
            }

            buttonNotificationListCancle.setOnClickListener {
                changeView(true)
            }

            buttonNotificationListDelete.setOnClickListener {
                // 선택된 메뉴 지우기
                removeData()
                changeView(true)
            }
        }
    }

    private fun changeView(isListView: Boolean) {
        binding.run {
            if (isListView) {
                layoutNotificationListBottomButton.visibility = View.GONE
                checkboxNotificationListSelectAll.visibility = View.GONE
                materialToolbarNotificationList.menu.findItem(R.id.menu_notification_list_delete).isVisible =
                    true
                materialToolbarNotificationList.setNavigationIcon(R.drawable.arrow_back_24px)

                // 체크박스 숨기기
                notificationListAdapter.updateCheckbox(false)
                notificationListAdapter.notifyDataSetChanged()

                // 알림 없음 메시지
                textViewNotificationListEmptyMsg.visibility =
                    if (dataSet.size <= 0) View.VISIBLE else View.GONE

            } else {
                layoutNotificationListBottomButton.visibility = View.VISIBLE
                checkboxNotificationListSelectAll.visibility = View.VISIBLE
                materialToolbarNotificationList.menu.findItem(R.id.menu_notification_list_delete).isVisible =
                    false
                materialToolbarNotificationList.navigationIcon = null

                // 체크 초기화
                if (dataSet.size == 0) {
                    setCheckBoxParentStete()
                } else {
                    for (data in dataSet) {
                        data.isCheck = false
                    }
                }

                // 체크박스 보이기
                notificationListAdapter.updateCheckbox(true)
                notificationListAdapter.notifyDataSetChanged()

            }
        }
    }

    private fun getData() {
        db.collection("notif_info")
            .where(
                Filter.and(
                    Filter.equalTo("is_deleted", false),
                    Filter.equalTo("receiver", UserModel.roleId)
                )
            )
            .get()
            .addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    dataSet.clear()

                    val datas = it.result

                    for (data in datas) {
                        val content = data.getString("content")!!
                        val date = data.getString("date")!!

                        // 날자 계산 필요. pattern: yyMMddHHmmssSSS
                        val tempDate = SimpleDateFormat("yyMMddHHmmssSSS").parse(date)
                        val now = MainActivity.getMilliSec()
                        val year = now.substring(0, 2).toInt() - date.substring(0, 2).toInt()
                        val month = now.substring(2, 4).toInt() - date.substring(2, 4).toInt()
                        val day = now.substring(4, 6).toInt() - date.substring(4, 6).toInt()

                        var dateStr = ""
                        if (1 < year) {
                            // 1년이 지났다면 연도로 표기
                            if (month < 1) {
                                dateStr =
                                    SimpleDateFormat("M월 d일", Locale.getDefault()).format(tempDate)
                            } else {
                                dateStr =
                                    SimpleDateFormat("YYYY년", Locale.getDefault()).format(tempDate)
                            }
                        } else if (1 < day) {
                            // 1년이 지나지 않았다면 00월 00일로 표기
                            dateStr =
                                SimpleDateFormat("M월 d일", Locale.getDefault()).format(tempDate)
                        } else if (1 == day) {
                            dateStr = "어제"
                        } else {
                            // 하루 이내의 시간은 오전/후 hh:mm 로 표기
                            dateStr =
                                SimpleDateFormat("aa h:m", Locale.getDefault()).format(tempDate)
                        }

                        val is_chat = data.getBoolean("is_chat")!!
                        val is_read = data.getBoolean("is_read")!!

                        val sender = data.getString("sender")!!
                        var senderName = "--"
                        if (sender == "agijagi") {
                            senderName = "아기자기"
                        } else {
                            if (UserModel.isSeller == true) {
                                db.collection("buyer").document(sender).get()
                                    .addOnCompleteListener { itTask ->
                                        if (itTask.isSuccessful) {
                                            val id = data.id
                                            dataSet.find { it.id == id }?.senderName =
                                                itTask.result.data?.get("nickname") as String

                                            notificationListAdapter.notifyDataSetChanged()
                                        }
                                    }
                            } else {
                                db.collection("seller").document(sender).get()
                                    .addOnCompleteListener { itTask ->
                                        if (itTask.isSuccessful) {
                                            val id = data.id
                                            dataSet.find { it.id == id }?.senderName =
                                                itTask.result.data?.get("nickname") as String

                                            notificationListAdapter.notifyDataSetChanged()
                                        }
                                    }
                            }
                        }

                        val type =
                            if (is_chat) NotificationType.NOTIF_CHAT else NotificationType.NOTIF_MESSAGE

                        val model = NotificationListModel(
                            data.id,
                            sender,
                            senderName,
                            content,
                            date,
                            dateStr,
                            type.str,
                            is_read
                        )
                        dataSet.add(model)
                    }

                    // 화면 갱신
                    dataSet.sortByDescending { it.date }
                    changeView(true)

                } else {
                    Log.e("FirebaseException", it.exception.toString())
                }
            }
    }

    private fun updateIsRead(notifID: String) {
        db.collection("notif_info").document(notifID)
            .update("is_read", true)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                } else {
                    Log.e("FirebaseException", it.exception.toString())
                }
            }
    }

    private fun removeData() {
        // 연결 리스트 해제
        val tempList = dataSet.filter { it.isCheck }
        dataSet.removeAll(tempList)

        var ch = true
        for (data in tempList) {
            db.collection("notif_info").document(data.id)
                .update("is_deleted", true)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                    } else {
                        ch = false
                    }
                }
        }

        if (!ch) {
            Log.e("FirebaseException", "1개 이상의 통신 에러 발생")
        }
    }

    private fun setCheckBoxParentStete() {
        val checkedCount = dataSet.filter { item -> item.isCheck }.size
        var state = -1
        var str: String = " 전체 선택"
        when (checkedCount) {
            0 -> {
                state = MaterialCheckBox.STATE_UNCHECKED
            }

            dataSet.size -> { // ALL
                state = MaterialCheckBox.STATE_CHECKED
                str = " 선택 해제"
            }

            else -> {
                state = MaterialCheckBox.STATE_INDETERMINATE
                str = " 전체 ${checkedCount}개"
            }
        }

        binding.checkboxNotificationListSelectAll.checkedState = state
        binding.checkboxNotificationListSelectAll.text = str
    }

    fun setup() {
        db = Firebase.firestore

        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}