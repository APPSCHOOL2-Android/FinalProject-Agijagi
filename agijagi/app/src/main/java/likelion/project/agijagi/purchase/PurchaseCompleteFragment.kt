package likelion.project.agijagi.purchase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentPurchaseCompleteBinding

class PurchaseCompleteFragment : Fragment() {

    private var _binding: FragmentPurchaseCompleteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPurchaseCompleteBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            buttonPurchaseComplete.setOnClickListener {
                it.findNavController().navigate(R.id.action_purchaseCompleteFragment_to_homeFragment)
            }
        }
    }

}