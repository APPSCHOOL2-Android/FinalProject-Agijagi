package likelion.project.agijagi.deliverytracking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentDeliveryTrackingBinding

class DeliveryTrackingFragment : Fragment() {

    private var _binding: FragmentDeliveryTrackingBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDeliveryTrackingBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            toolbarDeliveryTracking.run {
                setNavigationOnClickListener {
                    it.findNavController().navigate(R.id.action_deliveryTrackingFragment_to_orderFragment)
                }
            }

            buttonDeliveryTrackingCourier.setOnClickListener {
                Snackbar.make(it, "통화 1588-0000", Snackbar.LENGTH_LONG).setAction("확인", null).show()
            }

            buttonDeliveryTrackingShippingDriver.setOnClickListener {
                Snackbar.make(it, "통화 010-1234-5678", Snackbar.LENGTH_LONG).setAction("확인", null).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}