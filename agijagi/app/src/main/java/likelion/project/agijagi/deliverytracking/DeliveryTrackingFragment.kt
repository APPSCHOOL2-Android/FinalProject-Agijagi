package likelion.project.agijagi.deliverytracking

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.bumptech.glide.Glide
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.FragmentDeliveryTrackingBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DeliveryTrackingFragment : Fragment() {

    private var _binding: FragmentDeliveryTrackingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeliveryTrackingBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBundleData()

        binding.run {
            toolbarDeliveryTracking.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            buttonDeliveryTrackingCourier.setOnClickListener {
                Snackbar.make(it, "통화 1588-0000", Snackbar.LENGTH_LONG).setAction("확인", null).show()
            }

            buttonDeliveryTrackingShippingDriver.setOnClickListener {
                Snackbar.make(it, "통화 010-1234-5678", Snackbar.LENGTH_LONG).setAction("확인", null)
                    .show()
            }
        }
    }

    private fun setBundleData() {
        val bundle = arguments

        if (bundle == null) {
            // 일어날 수 없음.
            Log.e("BundleException", "Bundle is null!!")
            return
        }

        binding.run {
            val state = bundle.getString("state")!!
            when (state) {
                "배송 완료" -> {
                    imageViewDeliveryTrackingDeliveryReady.isActivated = true
                    imageViewDeliveryTrackingDeliveryShipping.isActivated = true
                    imageViewDeliveryTrackingDeliveryComplete.isActivated = true

                    // 배송 완료
                    textViewDeliveryTrackingArrivalInformation.text = "상품 배송 완료"
                }

                "배송 중" -> {
                    imageViewDeliveryTrackingDeliveryReady.isActivated = true
                    imageViewDeliveryTrackingDeliveryShipping.isActivated = true
                    imageViewDeliveryTrackingDeliveryComplete.isActivated = false

                    // 내일 상품 도착 예정
                    val calendar = Calendar.getInstance()
                    calendar.time = Date(System.currentTimeMillis())
                    calendar.add(Calendar.DATE, 1) // 하루 추가
                    val msg =
                        SimpleDateFormat(
                            "M/d (E) 상품 도착 예정",
                            Locale.getDefault()
                        ).format(calendar.time)
                    textViewDeliveryTrackingArrivalInformation.text = msg
                }

                else -> {
                    imageViewDeliveryTrackingDeliveryReady.isActivated = true
                    imageViewDeliveryTrackingDeliveryShipping.isActivated = false
                    imageViewDeliveryTrackingDeliveryComplete.isActivated = false

                    // 3일 후 도착 예정
                    val calendar = Calendar.getInstance()
                    calendar.time = Date(System.currentTimeMillis())
                    calendar.add(Calendar.DATE, 3) // 3일 추가
                    val msg =
                        SimpleDateFormat(
                            "M/d (E) 상품 도착 예정",
                            Locale.getDefault()
                        ).format(calendar.time)
                    textViewDeliveryTrackingArrivalInformation.text = msg
                }
            }

            val thumbnailUri = bundle.getString("thumbnail_image")!!
            Glide.with(binding.root)
                .load(thumbnailUri)
                .placeholder(R.drawable.order_default_image)
                .centerCrop()
                .into(imageViewDeliveryTrackingProduct)

            val prodName = bundle.getString("name")!!
            textViewDeliveryTrackingProductName.text = prodName

            // 운송장번호 14~a자리 랜덤숫자
            textViewDeliveryTrackingTrackingNumber.text = Math.random().toString().substring(2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}