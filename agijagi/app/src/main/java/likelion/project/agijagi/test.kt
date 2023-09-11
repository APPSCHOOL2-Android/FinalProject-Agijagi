package likelion.project.agijagi

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class test {
    companion object {
        val db = Firebase.firestore
        var auth = FirebaseAuth.getInstance()
        val userEmail = auth.currentUser?.email.toString()
    }
}