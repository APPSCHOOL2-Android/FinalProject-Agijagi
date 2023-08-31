package likelion.project.agijagi.sellermypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentProductDetailPreviewBinding

class ProductDetailPreviewFragment : Fragment() {

    private var binding: FragmentProductDetailPreviewBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductDetailPreviewBinding.inflate(inflater)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clickProductRegistration()
    }

    private fun clickProductRegistration() {
        binding?.run {
            buttonProductDetailPreviewProductRegistration.setOnClickListener {
                Snackbar.make(it, "상품 등록이 완료되었습니다.", Snackbar.LENGTH_SHORT).show()
                it.findNavController()
                    .navigate(R.id.action_productDetailPreviewFragment_to_productListFragment)
            }
        }
    }

}