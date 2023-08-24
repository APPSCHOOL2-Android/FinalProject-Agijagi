package likelion.project.agijagi.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentOrderDetailBinding

class OrderDetailFragment : Fragment() {

    lateinit var fragmentOrderDetailBinding: FragmentOrderDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentOrderDetailBinding = FragmentOrderDetailBinding.inflate(inflater)

        fragmentOrderDetailBinding.run {
            toolbarOrderDetail.run {
                setNavigationOnClickListener {
                    it.findNavController().navigate(R.id.action_orderDetailFragment_to_orderFragment)
                }
            }
        }

        return fragmentOrderDetailBinding.root
    }

}