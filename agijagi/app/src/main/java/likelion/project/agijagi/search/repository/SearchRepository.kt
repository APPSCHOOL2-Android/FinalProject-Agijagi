package likelion.project.agijagi.search.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SearchRepository {
    val db = FirebaseFirestore.getInstance()

    suspend fun getProductNameAndBrand(): List<String> {
        return withContext(Dispatchers.IO) {
            val querySnapshot = db.collection("product").get().await()
            val list = mutableListOf<String>()
            for (document in querySnapshot) {
                val name = document.getString("name") ?: ""
                val brand = document.getString("brand") ?: ""
                list.add(name)
                list.add(brand)
            }
            Log.d("hye repo", list.toString())
            list
        }
    }
}