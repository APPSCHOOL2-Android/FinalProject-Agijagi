package likelion.project.agijagi.deliverytracking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentDeliveryTrackingBinding

class DeliveryTrackingFragment : Fragment() {

    lateinit var fragmentDeliveryTrackingBinding: FragmentDeliveryTrackingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentDeliveryTrackingBinding = FragmentDeliveryTrackingBinding.inflate(layoutInflater)

        fragmentDeliveryTrackingBinding.run {
            toolbarDeliverytracking.run {
                setNavigationOnClickListener {
                    it.findNavController().navigate(R.id.action_deliveryTrackingFragment_to_orderFragment)
                }
            }
        }

        return fragmentDeliveryTrackingBinding.root
    }

}