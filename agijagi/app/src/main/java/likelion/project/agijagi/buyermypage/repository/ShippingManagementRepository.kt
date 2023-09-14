package likelion.project.agijagi.buyermypage.repository

import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import likelion.project.agijagi.UserEssential.Companion.auth

class ShippingManagementRepository {

    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userUid = auth.currentUser?.uid.toString()

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
                val basicFieldValue = documentSnapshot.getString("basic").toString()

                onSuccess(basicFieldValue)
            }
    }
}