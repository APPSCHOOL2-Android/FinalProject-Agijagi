package likelion.project.agijagi.productoption

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentReadyMadeOptionBinding

class ReadyMadeOptionFragment : Fragment() {

    private var _binding: FragmentReadyMadeOptionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReadyMadeOptionBinding.inflate(inflater)

        setToolbarItemAction()
        setShoppingBagButton()
        setPurchaseButton()

        return binding.root
    }

    private fun setToolbarItemAction() {
        binding.toolbarReadyMadeOption.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setShoppingBagButton() {
        binding.imageButtonReadyMadeOptionShoppingBag.setOnClickListener {
            findNavController().navigate(R.id.action_readyMadeOptionFragment_to_shoppingListFragment)
        }
    }

    private fun setPurchaseButton() {
        binding.buttonReadyMadeOptionPurchase.setOnClickListener {
            findNavController().navigate(R.id.action_readyMadeOptionFragment_to_paymentFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}