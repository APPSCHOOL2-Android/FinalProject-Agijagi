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

    lateinit var fragmentPurchaseCompleteBinding: FragmentPurchaseCompleteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentPurchaseCompleteBinding = FragmentPurchaseCompleteBinding.inflate(layoutInflater)

        fragmentPurchaseCompleteBinding.run {
            buttonPurchaseComplete.setOnClickListener {
                it.findNavController().navigate(R.id.action_purchaseCompleteFragment_to_homeFragment)
            }
        }
        return fragmentPurchaseCompleteBinding.root
    }

}