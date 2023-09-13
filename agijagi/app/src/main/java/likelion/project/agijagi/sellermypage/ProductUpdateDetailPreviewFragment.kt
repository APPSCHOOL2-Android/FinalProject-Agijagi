package likelion.project.agijagi.sellermypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentProductDetailPreviewBinding

class ProductUpdateDetailPreviewFragment : Fragment() {

    private var _binding: FragmentProductDetailPreviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailPreviewBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarItemAction()
        setProductUpdateButton()
    }

    private fun setToolbarItemAction() {
        binding.toolbarProductDetailPreview.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setProductUpdateButton() {
        binding.buttonProductDetailPreviewProductRegistration.run {
            text = "수정"
            setOnClickListener {
                Snackbar.make(it, "상품 수정이 완료되었습니다.", Snackbar.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_productUpdateDetailPreviewFragment_to_productListFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}