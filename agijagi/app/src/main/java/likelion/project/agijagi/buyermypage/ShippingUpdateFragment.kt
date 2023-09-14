package likelion.project.agijagi.buyermypage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import likelion.project.agijagi.R
import likelion.project.agijagi.buyermypage.repository.ShippingManagementRepository
import likelion.project.agijagi.databinding.FragmentShippingUpdateBinding
import likelion.project.agijagi.model.ShippingAddress

class ShippingUpdateFragment : Fragment() {

    private var _binding: FragmentShippingUpdateBinding? = null
    private val binding get() = _binding!!
    lateinit var shippingUpdateUid: String
    private val shippingManagementRepository = ShippingManagementRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShippingUpdateBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAddressUid()
        getShippingData()
        setupTextChangeListeners()
        setToolbarItemAction()
    }

    // bundle로 넘겨받은 주소uid
    private fun getAddressUid() {
        shippingUpdateUid = arguments?.getString("shippingUpdate").toString()
        Log.d("shippingUpdate", shippingUpdateUid)
    }

    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트 변경 이전에 호출
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경될 때 호출
            }

            override fun afterTextChanged(s: Editable?) {
                // 텍스트 변경 후에 호출
                updateButtonStateAndAction()
            }
        }
    }

    // 수정할 배송지 정보 불러오기
    private fun getShippingData() {
        shippingManagementRepository.getShippingData(shippingUpdateUid,
            onSuccess = { shippingData ->
                shippingData?.let { data ->
                    // 데이터가 null이 아닌 경우 UI 업데이트 수행
                    binding.run {
                        editinputShippingUpdateTitle.setText(data.shippingName)
                        editinputShippingUpdateAddress.setText(data.address)
                        editinputShippingUpdateAddressDetail.setText(data.addressDetail)
                        editinputShippingUpdateRecipient.setText(data.recipient)
                        editinputShippingUpdateRecipientPhone.setText(data.phoneNumber)
                    }
                } ?: run {
                    // 데이터가 null인 경우 처리
                    Log.e("ShippingUpdateFragment", "해당 배송지 데이터 없음")
                }
            }
        )
        shippingManagementRepository.getBasicShippingAddress { basicFieldValue ->
            binding.checkBoxShippingUpdateBasic.isChecked = basicFieldValue == shippingUpdateUid
        }
    }

    // 입력칸에 대한 텍스트 변경 리스너
    private fun setupTextChangeListeners() {
        binding.run {
            editinputShippingUpdateTitle.addTextChangedListener(createTextWatcher())
            editinputShippingUpdateAddress.addTextChangedListener(createTextWatcher())
            editinputShippingUpdateAddressDetail.addTextChangedListener(createTextWatcher())
            editinputShippingUpdateRecipient.addTextChangedListener(createTextWatcher())
            editinputShippingUpdateRecipientPhone.addTextChangedListener(createTextWatcher())
        }
    }

    // 입력사항 검사
    private fun isCheckShippingRegistrationInput(): Boolean {
        binding.run {
            val title = editinputShippingUpdateTitle.text.toString()
            val address = editinputShippingUpdateAddress.text.toString()
            val addressDetail = editinputShippingUpdateAddressDetail.text.toString()
            val recipient = editinputShippingUpdateRecipient.text.toString()
            val recipientPhone = editinputShippingUpdateRecipientPhone.text.toString()

            return title.isNotBlank() && address.isNotBlank() && addressDetail.isNotBlank() && recipient.isNotBlank() && recipientPhone.isNotBlank()
        }
    }

    // 입력사항이 있을 때 버튼 활성화 및 동작
    private fun updateButtonStateAndAction() {
        binding.run {
            buttonShippingUpdateRegistration.isEnabled = isCheckShippingRegistrationInput()

            if (isCheckShippingRegistrationInput()) {
                buttonShippingUpdateRegistration.run {
                    setBackgroundResource(R.drawable.wide_box_bottom_active)
                    setTextColor(ContextCompat.getColor(context, R.color.jagi_ivory))
                    setOnClickListener {
                        // 배송지 수정
                        updateShippingData()
                        Snackbar.make(it, "배송지 수정이 완료되었습니다.", Snackbar.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                }
            } else {
                buttonShippingUpdateRegistration.run {
                    setBackgroundResource(R.drawable.wide_box_bottom_inactive)
                    setTextColor(ContextCompat.getColor(context, R.color.jagi_black_42))
                }
            }
        }
    }

    // 데이터 수정
    private fun updateShippingData() {
        binding.run {
            val title = editinputShippingUpdateTitle.text.toString()
            val address = editinputShippingUpdateAddress.text.toString()
            val addressDetail = editinputShippingUpdateAddressDetail.text.toString()
            val recipient = editinputShippingUpdateRecipient.text.toString()
            val recipientPhone = editinputShippingUpdateRecipientPhone.text.toString()
            val basic = checkBoxShippingUpdateBasic.isChecked

            val shippingAddress = ShippingAddress(
                shippingUpdateUid,
                address,
                addressDetail,
                recipientPhone,
                recipient,
                title
            )
            shippingManagementRepository.updateShippingData(shippingAddress, basic)
        }
    }

    private fun setToolbarItemAction() {
        binding.toolbarShippingUpdate.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}