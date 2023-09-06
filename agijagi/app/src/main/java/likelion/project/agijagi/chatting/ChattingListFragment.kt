package likelion.project.agijagi.chatting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentChattingListBinding

class ChattingListFragment : Fragment() {

    private var _binding: FragmentChattingListBinding? = null
    private val binding get() = _binding!!

    lateinit var mainActivity: MainActivity
    lateinit var chattingListAdapter: ChattingListAdapter

    companion object {
        val dataSet = arrayListOf<ChattingListModel>().apply {
            add(ChattingListModel("홍길동", "채팅내용채팅내용채팅내용", "2022. 08. 15"))
            add(ChattingListModel("고길동", "내용입니다", "08월 16일", true, true))
            add(
                ChattingListModel(
                    "김길동",
                    "아주긴내용아주긴내용아주긴내용아주긴내용아주긴내용\n아주긴내용아주긴내용",
                    "어제"
                )
            )
            add(
                ChattingListModel(
                    "이길동",
                    "커스텀해주세요",
                    "오전 12:50", true, true
                )
            )
        }
    }

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

        dataSet.reverse()

        binding.run {
            // 초기화
            changeView(true)

            recyclerviewChattingList.run {
                layoutManager = LinearLayoutManager(mainActivity)
                adapter = chattingListAdapter

                addItemDecoration(
                    MaterialDividerItemDecoration(
                        context,
                        MaterialDividerItemDecoration.VERTICAL
                    )
                )
            }
            chattingListAdapter.submitList(dataSet)


            materialToolbarChattingList.run {
                setOnMenuItemClickListener {
                    changeView(false)
                    false
                }
            }

            buttonChattingListCancel.setOnClickListener {
                changeView(true)
            }

            buttonChattingListDelete.setOnClickListener {
                // 선택된 메뉴 지우기
                // dataSet.removeIf {it.isCheck}

                changeView(true)
            }

        }
    }

    private fun changeView(isListView: Boolean) {
        binding.run {
            if (isListView) {
                layoutChattingListBottomButton.visibility = View.GONE
                materialToolbarChattingList.run {
                    menu.findItem(R.id.menu_notification_list_delete).isVisible = true
                    setNavigationIcon(R.drawable.arrow_back_24px)
                }

                // 체크박스 숨기기
                chattingListAdapter.updateCheckbox(false)
                chattingListAdapter.notifyDataSetChanged()

                // 알림 없음 메시지
                textviewChattingListEmptyMsg.visibility =
                    if (dataSet.size <= 0) View.VISIBLE else View.GONE

            } else {
                layoutChattingListBottomButton.visibility = View.VISIBLE

                materialToolbarChattingList.run {
                    menu.findItem(R.id.menu_notification_list_delete).isVisible = false
                    navigationIcon = null
                }

                // 체크박스 보이기
                chattingListAdapter.updateCheckbox(true)
                chattingListAdapter.notifyDataSetChanged()

                // 체크 초기화
//                for (data in dataSet) {
//                    data.isCheck = false
//                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}