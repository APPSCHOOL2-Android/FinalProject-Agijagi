package likelion.project.agijagi.buyermypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import likelion.project.agijagi.databinding.FragmentShippingUpdateBinding

class ShippingUpdateFragment : Fragment() {

    private var _binding: FragmentShippingUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShippingUpdateBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarItemAction()
        setRegistrationButton()
    }

    private fun setToolbarItemAction() {
        binding.toolbarShippingUpdate.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setRegistrationButton() {
        binding.buttonShippingUpdateRegistration.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}