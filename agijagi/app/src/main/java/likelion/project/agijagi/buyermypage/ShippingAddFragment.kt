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

        setShippingRegistrationButton()

        return binding.root
    }

    private fun setShippingRegistrationButton() {
        binding.buttonShippingAddRegistration.setOnClickListener {
            Snackbar.make(it, "배송지 등록이 왼료되었습니다.", Snackbar.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_shippingAddFragment_to_shippingManagementFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}