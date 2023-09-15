package likelion.project.agijagi.buyermypage.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import likelion.project.agijagi.model.ShippingAddress

class ShippingManagementRepository {

    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userUid = auth.currentUser?.uid.toString()
    lateinit var basicFieldValue: String

    // 배송지 삭제
    fun deleteShippingAddress(uid: String) {
        db.collection("buyer").document(userUid)
            .collection("shipping_address")
            .document(uid).delete()
    }

    // 기본배송지 id 불러오기
    fun getBasicShippingAddress(onSuccess: (String) -> Unit) {
        db.collection("buyer").document(userUid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                basicFieldValue = documentSnapshot.getString("basic").toString()

                onSuccess(basicFieldValue)
            }
    }

    // 배송지 데이터 가져오기
    fun getShippingData(shippingUpdateUid: String, onSuccess: (ShippingAddress?) -> Unit) {
        val shippingAddressRef = db.collection("buyer").document(userUid)
            .collection("shipping_address").document(shippingUpdateUid)

        shippingAddressRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val title = documentSnapshot.getString("shipping_name").toString()
                    val address = documentSnapshot.getString("address").toString()
                    val addressDetail = documentSnapshot.getString("address_detail").toString()
                    val recipient = documentSnapshot.getString("recipient").toString()
                    val recipientPhone = documentSnapshot.getString("phone_number").toString()

                    val shippingData = ShippingAddress(
                        shippingUpdateUid,
                        address,
                        addressDetail,
                        recipientPhone,
                        recipient,
                        title
                    )

                    onSuccess(shippingData)
                } else {
                    // 해당 배송지 데이터가 존재하지 않을 경우 null을 반환합니다.
                    onSuccess(null)
                }
            }
    }

    // 배송지 데이터 수정하기
    fun updateShippingData(shippingAddress: ShippingAddress, basic: Boolean) {
        val shippingInfo = hashMapOf(
            "address" to shippingAddress.address,
            "address_detail" to shippingAddress.addressDetail,
            "phone_number" to shippingAddress.phoneNumber,
            "recipient" to shippingAddress.recipient,
            "shipping_name" to shippingAddress.shippingName
        )

        val userUid = auth.currentUser?.uid.toString()
        val shippingAddressRef = db.collection("buyer").document(userUid)
            .collection("shipping_address").document(shippingAddress.shippingAddressId)

        shippingAddressRef.set(shippingInfo, SetOptions.merge())
            .addOnSuccessListener {
                // 기본배송지 선택된 경우 basic 필드 업데이트
                if (basic) {
                    updateBasicShippingAddress(shippingAddress.shippingAddressId)
                }
            }
    }

    // 기본 배송지 수정하기
    private fun updateBasicShippingAddress(shippingId: String) {
        db.collection("buyer").document(userUid).update("basic", shippingId)
    }
}