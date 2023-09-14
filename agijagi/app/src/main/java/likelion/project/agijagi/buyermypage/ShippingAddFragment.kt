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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.R
import likelion.project.agijagi.UserEssential
import likelion.project.agijagi.UserEssential.Companion.db
import likelion.project.agijagi.UserEssential.Companion.roleId
import likelion.project.agijagi.databinding.FragmentShippingAddBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ShippingAddFragment : Fragment() {

    private var _binding: FragmentShippingAddBinding? = null
    private val binding get() = _binding!!

    private var auth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShippingAddBinding.inflate(inflater)
        auth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setup()
        setToolbarItemAction()
        updateButtonStateAndAction()
//        setShippingRegistrationButton()
        setupTextChangeListeners()

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
                        addShippingData()
                        Snackbar.make(it, "배송지 등록이 완료되었습니다.", Snackbar.LENGTH_SHORT).show()
                        findNavController().popBackStack()
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
    private fun addShippingData() {
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

            val userUid = auth?.currentUser?.uid.toString()

            // 데이터저장 테스트코드 (db, auth, setup 같이 지울코드)
            db.collection("buyer").document(userUid)
                .collection("shipping_address")
                .add(shippingInfo)
                .addOnCompleteListener {
                    val newShippingId = it.result.id
                    if (basic) {
                        updateBasicShippingAddress(newShippingId)
                    }
                    Log.d("shippingAdd", "데이터저장 성공")
                }.addOnFailureListener {
                    Log.d("shippingAdd", "데이터저장 실패")
                }

//            // 데이터저장 userEssential 사용코드
//            db.collection("buyer").document(roleId)
//                .collection("shipping_address")
//                .add(shippingInfo)
//                .addOnCompleteListener {
//                    val newShippingId = it.result.id
//                    if (basic) {
//                        updateBasicShippingAddress(newShippingId)
//                    }
//                    Log.d("shippingAdd", "데이터저장 성공")
//                }.addOnFailureListener {
//                    Log.d("shippingAdd", "데이터저장 실패")
//                }
        }
    }

    // 기본배송지 설정
    private fun updateBasicShippingAddress(shippingId: String) {
        val userUid = auth?.currentUser?.uid.toString()
        val dbCollection = db.collection("buyer").document(userUid)

        dbCollection.update("basic", shippingId)
            .addOnSuccessListener {
                Log.d("updateBasicShipping", "기본 배송지 업데이트 성공")
            }
            .addOnFailureListener {
                Log.e("updateBasicShipping", "기본 배송지 업데이트 실패")
            }
    }

    private fun setup() {
        db = Firebase.firestore

        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
    }
    private fun setToolbarItemAction() {
        binding.toolbarShippingAdd.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setShippingRegistrationButton() {
        binding.buttonShippingAddRegistration.setOnClickListener {
            Snackbar.make(it, "배송지 등록이 왼료되었습니다.", Snackbar.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}