package likelion.project.agijagi.buyermypage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentShippingAddBinding
import likelion.project.agijagi.model.UserModel

class ShippingAddFragment : Fragment() {

    private var _binding: FragmentShippingAddBinding? = null
    private val binding get() = _binding!!

    val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShippingAddBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarItemAction()
        updateButtonStateAndAction()
        setupTextChangeListeners()
    }

    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            // 텍스트 변경 후에 호출
            override fun afterTextChanged(s: Editable?) {
                updateButtonStateAndAction()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    // 입력칸에 대한 텍스트 변경 리스너
    private fun setupTextChangeListeners() {
        binding.run {
            editinputShippingAddTitle.addTextChangedListener(createTextWatcher())
            editinputShippingAddAddress.addTextChangedListener(createTextWatcher())
            editinputShippingAddAddressDetail.addTextChangedListener(createTextWatcher())
            editinputShippingAddRecipient.addTextChangedListener(createTextWatcher())
            editinputShippingAddRecipientPhone.addTextChangedListener(createTextWatcher())
        }
    }

    // 입력사항 검사
    private fun isCheckShippingRegistrationInput(): Boolean {
        binding.run {
            val title = editinputShippingAddTitle.text.toString()
            val address = editinputShippingAddAddress.text.toString()
            val addressDetail = editinputShippingAddAddressDetail.text.toString()
            val recipient = editinputShippingAddRecipient.text.toString()
            val recipientPhone = editinputShippingAddRecipientPhone.text.toString()

            return title.isNotBlank() && address.isNotBlank() && addressDetail.isNotBlank() && recipient.isNotBlank() && recipientPhone.isNotBlank()
        }
    }

    // 입력사항이 있을 때 버튼 활성화 및 동작
    private fun updateButtonStateAndAction() {
        binding.run {
            buttonShippingAddRegistration.isEnabled = isCheckShippingRegistrationInput()

            if (isCheckShippingRegistrationInput()) {
                buttonShippingAddRegistration.run {
                    setBackgroundResource(R.drawable.wide_box_bottom_active)
                    setTextColor(ContextCompat.getColor(context, R.color.jagi_ivory))
                    setOnClickListener {
                        // 배송지등록
                        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        addShippingData {
                            Snackbar.make(it, "배송지 등록이 완료되었습니다", Snackbar.LENGTH_SHORT)
                                .setAnchorView(buttonShippingAddRegistration)
                                .addCallback(object : Snackbar.Callback() {
                                    // 스낵바가 종료될 때의 처리
                                    override fun onDismissed(
                                        transientBottomBar: Snackbar?,
                                        event: Int
                                    ) {
                                        super.onDismissed(transientBottomBar, event)
                                        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                        // 화면이동
                                        findNavController().popBackStack()
                                    }
                                })
                                .show()
                        }
                    }
                }
            } else {
                buttonShippingAddRegistration.run {
                    setBackgroundResource(R.drawable.wide_box_bottom_inactive)
                    setTextColor(ContextCompat.getColor(context, R.color.jagi_black_42))
                }
            }
        }
    }

    // 데이터 저장
    private fun addShippingData(action: () -> Unit) {
        binding.run {
            val title = editinputShippingAddTitle.text.toString()
            val address = editinputShippingAddAddress.text.toString()
            val addressDetail = editinputShippingAddAddressDetail.text.toString()
            val recipient = editinputShippingAddRecipient.text.toString()
            val recipientPhone = editinputShippingAddRecipientPhone.text.toString()
            val basic = checkBoxShippingAddBasic.isChecked

            val shippingInfo = hashMapOf(
                "address" to address,
                "address_detail" to addressDetail,
                "phone_number" to recipientPhone,
                "recipient" to recipient,
                "shipping_name" to title
            )

            // 데이터 저장
            db.collection("buyer").document(UserModel.roleId)
                .collection("shipping_address")
                .add(shippingInfo)
                .addOnCompleteListener {
                    val newShippingId = it.result.id
                    if (basic) {
                        updateBasicShippingAddress(newShippingId)
                    }
                    action()

                    Log.d(
                        "ShippingAddFragment.addShippingData()",
                        "데이터저장 성공"
                    )
                }.addOnFailureListener {
                    Log.d(
                        "ShippingAddFragment.addShippingData()",
                        "데이터저장 실패: ${it.stackTrace}"
                    )
                }
        }
    }

    // 기본배송지 설정
    private fun updateBasicShippingAddress(shippingId: String) {
        val dbCollection = db.collection("buyer").document(UserModel.roleId)
        dbCollection.update("basic", shippingId)
            .addOnSuccessListener {
                Log.d(
                    "ShippingAddFragment.updateBasicShippingAddress()",
                    "기본 배송지 업데이트 성공"
                )
            }
            .addOnFailureListener {
                Log.e(
                    "ShippingAddFragment.updateBasicShippingAddress()",
                    "기본 배송지 업데이트 실패: ${it.stackTrace}"
                )
            }
    }

    private fun setToolbarItemAction() {
        binding.toolbarShippingAdd.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}