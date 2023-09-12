package likelion.project.agijagi.search.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SearchRepository {

    val db = FirebaseFirestore.getInstance()

    suspend fun getProductNameAndBrand(): List<String> {
        return withContext(Dispatchers.IO) {
            val querySnapshot = db.collection("product").get().await()
            val dataList = mutableListOf<String>()
            for (document in querySnapshot) {
                val name = document.getString("name") ?: ""
                val brand = document.getString("brand") ?: ""
                dataList.add(name)
                dataList.add(brand)
            }
            dataList
        }
    }

}