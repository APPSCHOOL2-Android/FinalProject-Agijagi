package likelion.project.agijagi.sellermypage

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentStoreManagementBinding
import likelion.project.agijagi.model.SellerModel
import likelion.project.agijagi.model.UserModel

class StoreManagementFragment : Fragment() {

    private var _binding: FragmentStoreManagementBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore

    private val permissionList = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_MEDIA_LOCATION
    )
    lateinit var callbackActionGranted: () -> Unit
    lateinit var callbackActionDenide: () -> Unit
    lateinit var albumActivityLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreManagementBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setup()

        setDefaultValues()
        setAlbumActivityLaunchers()
        setToolbarItemAction()
        setAddFileButton()
        setEditButton()
    }

    private fun setToolbarItemAction() {
        binding.toolbarStoreManagement.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for (idx in 0 until permissions.size) {
            // 현재 번째의 권한 이름을 가져온다.
            val p1 = permissions[idx]
            // 권한 허용 여부 값을 가져온다.
            val g1 = grantResults[idx]

            when (p1) {
                Manifest.permission.READ_EXTERNAL_STORAGE -> {
                    if (g1 == PackageManager.PERMISSION_GRANTED) {
                        callbackActionGranted()
                    } else {
                        callbackActionDenide()
                    }
                }

                Manifest.permission.ACCESS_MEDIA_LOCATION -> {
                    if (g1 == PackageManager.PERMISSION_GRANTED) {
                        callbackActionGranted()
                    } else {
                        callbackActionDenide()
                    }
                }
            }
        }
    }

    private fun setAlbumActivityLaunchers() {
        albumActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it?.resultCode != Activity.RESULT_OK) {
                    return@registerForActivityResult
                }
                // Uri 가져오기
                val uri = (it.data?.data) ?: return@registerForActivityResult

                // Filename 가져오기
                val filename = DocumentFile.fromSingleUri(requireContext(), uri)?.name
                    ?: return@registerForActivityResult

                binding.textviewStoreManagementFileName.text = filename
            }
    }

    private fun setDefaultValues() {
        binding.run {
            textviewStoreManagementEmail.text = UserModel.email
            editinputStoreManagementBusinessName.hint = SellerModel.businessName
            editinputStoreManagementBusinessLicenseNumber.hint = SellerModel.brn
            editinputStoreManagementRepresentativeName.hint = UserModel.name
            editinputStoreManagementCall.hint = SellerModel.tel
            editinputStoreManagementAddress.hint = SellerModel.address
            binding.textviewStoreManagementFileName.text = SellerModel.brCert

            if (UserModel.googleLoginCheck == true) {
                layoutStoreManagementGoogleHide.visibility = View.GONE
            } else { // null, false
                layoutStoreManagementGoogleHide.visibility = View.VISIBLE
            }
        }
    }

    private fun setAddFileButton() {
        // 사업자 등록증 이미지
        binding.buttonStoreManagementAddFile.setOnClickListener {
            requestPermissions(permissionList, 0)
            callbackActionGranted = {
                val newIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                newIntent.setType("image/*")
                val mimeType = arrayOf("image/*")
                newIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
                albumActivityLauncher.launch(newIntent)
            }
            callbackActionDenide = {
                Snackbar.make(it, "권한을 허용하세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setEditButton() {
        binding.run {
            buttonStoreManagementEdit.setOnClickListener {
                // 유효성 검사
                val businessName = editinputStoreManagementBusinessName.text.toString()
                val brn = editinputStoreManagementBusinessLicenseNumber.text.toString()
                val name = editinputStoreManagementRepresentativeName.text.toString()
                val tel = editinputStoreManagementCall.text.toString()
                val pw = editinputStoreManagementPassword.text.toString()
                val pwCk = editinputStoreManagementCheckPassword.text.toString()
                val address = editinputStoreManagementAddress.text.toString()
                val brCert = textviewStoreManagementFileName.text.toString()

                val userData = mutableMapOf<String, Any>()
                val sellerData = mutableMapOf<String, Any>()

                if (UserModel.googleLoginCheck != true) {
                    if (pw.length < 6) {
                        Snackbar.make(binding.root, "비밀번호를 6자리 이상 입력하세요", Snackbar.LENGTH_SHORT)
                            .setAnchorView(buttonStoreManagementEdit)
                            .setTextColor(resources.getColor(R.color.jagi_carrot))
                            .show()
                        return@setOnClickListener
                    }
                    if (pw != pwCk) {
                        Snackbar.make(binding.root, "비밀번호가 다릅니다!", Snackbar.LENGTH_SHORT)
                            .setAnchorView(buttonStoreManagementEdit)
                            .setTextColor(resources.getColor(R.color.jagi_carrot))
                            .show()
                        return@setOnClickListener
                    }
                    UserModel.password = pw
                    userData["password"] = pw
                }

                // 공통_seller
                if (businessName == "" || businessName == " ") {
                } else {
                    SellerModel.businessName = businessName
                    sellerData["businessName"] = businessName
                }
                if (brn == "" || brn == " ") {
                } else {
                    SellerModel.brn = brn
                    sellerData["brn"] = brn
                }
                if (tel == "" || tel == " ") {
                } else {
                    SellerModel.tel = tel
                    sellerData["tel"] = tel
                }
                if (address == "" || address == " ") {
                } else {
                    SellerModel.address = address
                    sellerData["address"] = address
                }
                if (brCert == "" || brCert == " ") {
                } else {
                    SellerModel.brCert = brCert
                    sellerData["brCert"] = brCert
                }

                // 공통_user
                if (name == "" || name == " ") {
                } else {
                    UserModel.name = name
                    userData["name"] = name
                }

                // 서버 저장
                db.collection("seller").document(UserModel.roleId).update(sellerData)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                        } else {
                            Log.e("FirebaseException", it.exception.toString())
                        }
                    }

                db.collection("user").document(UserModel.uid).update(userData)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            // 수정 알림과 이동
                            // 화면 터치 막기
                            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            Snackbar.make(binding.root, "수정되었습니다", Snackbar.LENGTH_SHORT)
                                .setAnchorView(buttonStoreManagementEdit)
                                .addCallback(object : Snackbar.Callback() {
                                    // 스낵바가 종료될 때의 처리
                                    override fun onDismissed(
                                        transientBottomBar: Snackbar?,
                                        event: Int
                                    ) {
                                        super.onDismissed(transientBottomBar, event)
                                        // 화면 터치 풀기
                                        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                        // 화면이동
                                        findNavController().popBackStack()
                                    }
                                })
                                .show()
                        } else {
                            Log.e("FirebaseException", it.exception.toString())
                        }
                    }
            }
        }
    }

    fun setup() {
        db = Firebase.firestore

        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}