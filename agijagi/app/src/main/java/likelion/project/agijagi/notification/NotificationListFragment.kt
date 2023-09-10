package likelion.project.agijagi.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.divider.MaterialDividerItemDecoration
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.chatting.ChattingListFragment
import likelion.project.agijagi.databinding.FragmentNotificationListBinding

class NotificationListFragment : Fragment() {

    private var _binding: FragmentNotificationListBinding? = null
    private val binding get() = _binding!!

    lateinit var mainActivity: MainActivity
    lateinit var notificationListAdapter: NotificationListAdapter

    val dataSet = arrayListOf<NotificationListModel>().apply {
        add(NotificationListModel("아기자기", "가입을 축하합니다!", "환영알림", "2023-08-15 22:10:10"))
        add(NotificationListModel("판매자1", "제목1", "내용1", "2023-08-16 06:10:10", true, true))
        add(
            NotificationListModel(
                "판매자2",
                "긴제목 긴제목 긴제목 긴제목 긴제목 긴제목 긴제목",
                "아주긴내용아주긴내용아주긴내용아주긴내용아주긴내용/n아주긴내용아주긴내용",
                "2023-08-20 12:10:10"
            )
        )
        add(
            NotificationListModel(
                "Agijagi",
                "도자기 입고 안내",
                "이제 도자기를 주문할 수 있습니다.",
                "2023-09-06 02:50:40", true, true
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationListBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        notificationListAdapter = NotificationListAdapter()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataSet.reverse()

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
            notificationListAdapter.setCheckBoxParentStete { setCheckBoxParentStete() }

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

                notificationListAdapter.notifyDataSetChanged()
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
                // dataSet.removeIf {it.isCheck}

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
                for (data in ChattingListFragment.dataSet) {
                    data.isCheck = false
                }

                // 체크박스 보이기
                notificationListAdapter.updateCheckbox(true)
                notificationListAdapter.notifyDataSetChanged()

            }
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
                str = " 전체 선택 해제"
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