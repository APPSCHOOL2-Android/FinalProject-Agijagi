package likelion.project.agijagi.chatting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentChattingRoomBinding

class ChattingRoomFragment : Fragment() {

    private var _binding: FragmentChattingRoomBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChattingRoomBinding.inflate(inflater)

        setToolbarItemAction()

        return binding.root
    }

    private fun setToolbarItemAction() {
        binding.toolbarChattingRoom.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}