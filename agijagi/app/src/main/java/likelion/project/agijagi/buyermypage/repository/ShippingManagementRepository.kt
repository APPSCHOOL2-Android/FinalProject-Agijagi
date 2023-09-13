package likelion.project.agijagi.buyermypage.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ShippingManagementRepository {


    fun deleteShippingAddress(uid: String){
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        val userUid = auth.currentUser?.uid.toString()

        db.collection("buyer").document(userUid)
            .collection("shipping_address")
            .document(uid).delete()
    }
}