package likelion.project.agijagi.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
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
            }
            notificationListAdapter.submitList(dataSet)

            buttonNotificationListCancle.setOnClickListener {
                changeView(true)
            }

            buttonNotificationListDelete.setOnClickListener {
                // 선택된 메뉴 지우기
                // dataSet.removeIf {it.isCheck}

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
                materialToolbarNotificationList.menu.findItem(R.id.menu_notification_list_delete).isVisible =
                    true
                materialToolbarNotificationList.setNavigationIcon(R.drawable.arrow_back_24px)

                // 체크박스 숨기기

                // 알림 없음 메시지
                textViewNotificationListEmptyMsg.visibility =
                    if (dataSet.size <= 0) View.VISIBLE else View.GONE

            } else {
                layoutNotificationListBottomButton.visibility = View.VISIBLE
                materialToolbarNotificationList.menu.findItem(R.id.menu_notification_list_delete).isVisible =
                    false
                materialToolbarNotificationList.navigationIcon = null

                // 체크 초기화
//                for (data in dataSet) {
//                    data.isCheck = false
//                }

                // 체크박스 보이기


            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}