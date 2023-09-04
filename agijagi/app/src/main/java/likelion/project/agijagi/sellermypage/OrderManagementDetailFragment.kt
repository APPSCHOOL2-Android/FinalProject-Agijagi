package likelion.project.agijagi.sellermypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentOrderManagementDetailBinding
import likelion.project.agijagi.sellermypage.model.OrderManagementModel
import likelion.project.agijagi.shopping.ShoppingListModel


class OrderManagementDetailFragment : Fragment() {
    private var _binding: FragmentOrderManagementDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderManagementDetailBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments?.getParcelable<OrderManagementModel>("orderManagementDetail") != null) {
            val item = arguments?.getParcelable<OrderManagementModel>("orderManagementDetail")
            val name = item?.name
            val price = item?.price

            binding.run {
                textviewOrderDetailName.text = name
                textviewOrderDetailPrice.text = price
            }

        }
    }
}