package likelion.project.agijagi.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentNotificationListBinding

class NotificationListFragment : Fragment() {

    private var _binding: FragmentNotificationListBinding? = null
    private val binding get() = _binding!!

    lateinit var mainActivity: MainActivity
    lateinit var notificationListAdapter: NotificationListAdapter

    private val dataSet = arrayListOf<NotificationListModel>()

    // 임시 코드
    lateinit var db: FirebaseFirestore
    fun setup() {
        db = Firebase.firestore

        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
    }

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
            notificationListAdapter.setGoToChat { roomID ->
                // 채팅방으로 이동하는 코드
                //findNavController().findDestination("")
                Snackbar.make(binding.root, "채팅방 이동: $roomID ", Snackbar.LENGTH_SHORT).show()
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
        val roleId = "testid" //테스트id

        db.collection("notif_info")
            .where(
                Filter.and(
                    Filter.equalTo("is_deleted", false),
                    Filter.equalTo("receiver", roleId)
                )
            )
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Snackbar.make(binding.root, "성공", Snackbar.LENGTH_SHORT).show()

                    dataSet.clear()

                    val datas = it.result
                    println("datas.size= ${datas.size()}") //2개가 정상

                    for (data in datas) {
                        val content = data.getString("content")!!
                        val date = data.getString("date")!!
                        val is_chat = data.getBoolean("is_chat")!!
                        val is_read = data.getBoolean("is_read")!!
                        val sender = data.getString("sender")!!

                        val type =
                            if (is_chat) NotificationType.NOTIF_CHAT else NotificationType.NOTIF_MESSAGE

                        val model = NotificationListModel(
                            data.id,
                            sender,
                            content,
                            date,
                            type.str,
                            is_read
                        )
                        dataSet.add(model)
                    }

                    // 화면 갱신
                    dataSet.sortByDescending { it.date }
                    changeView(true)

                } else {
                    Snackbar.make(binding.root, "실패", Snackbar.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateIsRead(notifID: String) {
        db.collection("notif_info").document(notifID)
            .update("is_read", true)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Snackbar.make(binding.root, "성공", Snackbar.LENGTH_SHORT).show()

                } else {
                    Snackbar.make(binding.root, "실패", Snackbar.LENGTH_SHORT).show()
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

        if (ch) {
            Snackbar.make(binding.root, "성공", Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(binding.root, "실패", Snackbar.LENGTH_SHORT).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}