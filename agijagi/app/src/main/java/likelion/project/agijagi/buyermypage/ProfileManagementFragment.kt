package likelion.project.agijagi.buyermypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentProfileManagementBinding

class ProfileManagementFragment : Fragment() {

    private var _binding: FragmentProfileManagementBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileManagementBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarItemAction()
        setEditButton()
    }

    private fun setToolbarItemAction() {
        binding.toolbarProfileManagement.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun setEditButton() {
        binding.buttonProfileManagementEdit.setOnClickListener {
            // 유효성 검사 추가 해야 함
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}