package likelion.project.agijagi.chatting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import likelion.project.agijagi.databinding.FragmentChattingRoomBinding
import likelion.project.agijagi.model.Message
import likelion.project.agijagi.model.UserModel

class ChattingRoomFragment : Fragment() {

    private var _binding: FragmentChattingRoomBinding? = null
    private val binding get() = _binding!!

    lateinit var chattingRoomAdapter: ChattingRoomAdapter

    val list = arrayListOf<Message>().apply {
        add(Message("09:00", "안녕하세요", true, UserModel.roleId))
        add(Message("10:12", "안녕하세요~~", true, "홍길동"))
        add(Message("11:09", "화려한 접시 커스텀 문구 수정 하고 싶어요화려한 접시 커스텀 문구 수정 하고 싶어요화려한 접시 커스텀 문구 수정 하고 싶어요화려한 접시 커스텀 문구 수정 하고 싶어요아주 긴 메시지 긴메세세ㅔ세세ㅔ세서ㅔㅓ매덪ㄴ제ㅐ어레내ㅓㅔㅓ메넝레ㅓ멘어레ㅓㅁㅇㄹㅁㅇㅓㅁㅅ", true, UserModel.roleId))
        add(Message("11:12", "네~", true, "홍길동"))
        add(Message("11:15", "멋쟁이 사자처럼 부트캠프입니당", true, UserModel.roleId))
        add(Message("11:20", "화려한 접시 커스텀 문구 수정 하고 싶어", true, "홍길동"))
        add(Message("11:23", "화려한 접시 커스텀", true, "홍길동"))
        add(Message("12:50", "멋쟁이 사자처럼", false, UserModel.roleId))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChattingRoomBinding.inflate(inflater)

        setupToolbar()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chattingRoomAdapter = ChattingRoomAdapter(UserModel.roleId)
        setupRecyclerViewChattingRoom(list)
    }

    private fun setupRecyclerViewChattingRoom(list: List<Message>) {
        binding.recyclerviewChattingRoom.run {
            adapter = chattingRoomAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        chattingRoomAdapter.submitList(list)
    }

    private fun setupToolbar() {
        binding.toolbarChattingRoom.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}