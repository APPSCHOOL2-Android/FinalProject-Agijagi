package likelion.project.agijagi.buyermypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentShippingAddBinding

class ShippingAddFragment : Fragment() {

    private var _binding: FragmentShippingAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShippingAddBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            buttonShippingAddRegistration.setOnClickListener {

            }
        }
        setToolbarItemAction()
        setShippingRegistrationButton()

    }

    private fun setToolbarItemAction() {
        binding.toolbarShippingAdd.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setShippingRegistrationButton() {
        binding.buttonShippingAddRegistration.setOnClickListener {
            Snackbar.make(it, "배송지 등록이 왼료되었습니다.", Snackbar.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}