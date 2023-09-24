package likelion.project.agijagi.purchase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentPurchaseCompleteBinding

class PurchaseCompleteFragment : Fragment() {

    private var _binding: FragmentPurchaseCompleteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPurchaseCompleteBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getPurchaseData()

        binding.run {
            buttonPurchaseComplete.setOnClickListener {
                it.findNavController()
                    .navigate(R.id.action_purchaseCompleteFragment_to_homeFragment)
            }
        }
    }

    // bundle로 넘겨받은 주소uid
    private fun getPurchaseData() {
        val orderId = arguments?.getString("orderId").toString()
        val orderRecipient = arguments?.getString("orderRecipient").toString()
        val orderMobile = arguments?.getString("orderMobile").toString()
        val orderAddress = arguments?.getString("orderAddress").toString()
        val orderAddressDetail = arguments?.getString("orderAddress_detail").toString()

        binding.run {
            textViewPurchaseCompleteOrderNumberValue.text = orderId
            textViewPurchaseCompleteCustomerName.text = orderRecipient
            textViewPurchaseCompleteCustomerPhone.text = orderMobile
            textViewPurchaseCompleteShippingAddressValue.text = orderAddress
            textViewPurchaseCompleteShippingAddressDetailValue.text = orderAddressDetail
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}